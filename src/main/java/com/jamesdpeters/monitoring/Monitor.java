package com.jamesdpeters.monitoring;

import com.jamesdpeters.GameBoy;
import com.jamesdpeters.Utils;
import com.jamesdpeters.cpu.CPU;
import com.jamesdpeters.memory.MemoryBus;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Monitor {

    public static final int START_CYCLE = 0;
    public static final int END_CYCLE = 25350000;

    public static ArrayList<CPUCycle> cpuCycleList = new ArrayList<>();

    public static void addCycle(CPUCycle cpuCycle){
        if(!MemoryBus.isBootRomEnabled) {
            if (GameBoy.getCpu().getRegisters().totalCycles >= START_CYCLE) {
                cpuCycleList.add(cpuCycle);
            }
            if (GameBoy.getCpu().getRegisters().totalCycles > END_CYCLE) {
                System.out.println("END OF MONITOR");
                GameBoy.instance.running = false;
                saveToCSV();
                System.exit(0);
            }
        }
    }

    public static void saveToCSV(){
        PrintWriter outputFile = getOutputFile(GameBoy.getCpu().getCart().getTitle()+".csv"); // this sends the output to file1
        if(outputFile != null) {
            // Write the file as a comma seperated file (.csv) so it can be read it into EXCEL
            outputFile.println("Total Cycles, PC, OpCode, Instruction, Cycle Amount, A, B, C, D, E, H, L, SP, Flags, IME");

            cpuCycleList.forEach(cpuCycle -> {
                String output = cpuCycle.getTotalCycles()+" , "+
                        Utils.intToString(cpuCycle.getPc())+", "+
                        cpuCycle.getCode()+", "+
                        cpuCycle.getInstructionName()+" , "+
                        cpuCycle.getCycleAmount()+", ";

                if(cpuCycle.getRegister() != null) {
                    output +=   Utils.intToString(cpuCycle.getRegister().getA()) + ", " +
                                Utils.intToString(cpuCycle.getRegister().getB()) + ", " +
                                Utils.intToString(cpuCycle.getRegister().getC()) + ", " +
                                Utils.intToString(cpuCycle.getRegister().getD()) + ", " +
                                Utils.intToString(cpuCycle.getRegister().getE()) + ", " +
                                Utils.intToString(cpuCycle.getRegister().getH()) + ", " +
                                Utils.intToString(cpuCycle.getRegister().getL()) + ", " +
                                Utils.intToString(cpuCycle.getRegister().sp) + ", "+
                                cpuCycle.getFlags() + ", "+
                                cpuCycle.getRegister().IME + ", ";
                }

                outputFile.println(output);
            });

            outputFile.close(); // close the output file
        }
    }

    private static PrintWriter getOutputFile(String filenamePath){
        try {
            Path path = Paths.get("outputs/"+filenamePath);
            Files.createDirectories(path.getParent());
            FileWriter file = new FileWriter(String.valueOf(path));     // this creates the file with the given name
            return new PrintWriter(file); // this sends the output to file
        } catch (IOException e) {
            System.err.println("File couldn't be accessed it may be being used by another process!");
            System.err.println("Close the file and press Enter to try again!");
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            try { reader.readLine(); } catch (IOException ex) { ex.printStackTrace(); }
            return getOutputFile(filenamePath);
        }
    }
}
