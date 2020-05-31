package com.jamesdpeters.cpu;

import com.jamesdpeters.Utils;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

public class Registers {
    int a,b,c,d,e,h,l; //Registers

    public short sp; //Stack pointer
    public short pc; //Program counter/pointer
    FlagsRegister f = new FlagsRegister();

    public int getA() {
        return a;
    }

    public int getB() {
        return b;
    }

    public int getC() {
        return c;
    }

    public int getD() {
        return d;
    }

    public int getE() {
        return e;
    }

    public int getH() {
        return h;
    }

    public int getL() {
        return l;
    }

    public FlagsRegister getF(){
        return f;
    }

    public int getAF(){
        return a << 8 | f.get();
    }
    public int getBC(){
        return b << 8 | c;
    }
    public int getDE() { return d << 8 | e; }
    public int getHL() { return h << 8 | l; }

    public Registers setA(int a) {
        this.a = a;
        return this;
    }

    public Registers setB(int b) {
        this.b = b;
        return this;
    }

    public Registers setC(int c) {
        this.c = c;
        return this;
    }

    public Registers setD(int d) {
        this.d = d;
        return this;
    }

    public Registers setE(int e) {
        this.e = e;
        return this;
    }

    public Registers setH(int h) {
        this.h = h;
        return this;
    }

    public Registers setL(int l) {
        this.l = l;
        return this;
    }

    public Registers setAF(int AF){
        a = ((AF & 0xFF00) >> 8);
        f.setFlag((AF & 0xFF));
        return this;
    }

    public Registers setBC(int BC){
        b = ((BC & 0xFF00) >> 8);
        c = (BC & 0xFF);
        return this;
    }

    public Registers setDE(int DE){
        d = ((DE & 0xFF00) >> 8);
        e = (DE & 0xFF);
        return this;
    }

    public Registers setHL(int HL){
        h = ((HL & 0xFF00) >> 8);
        l = (HL & 0xFF);
        return this;
    }

    @Override
    public String toString() {
        return String.format("A=0x%08X, B=0x%08X, C=0x%08X, D=0x%08X, E=0x%08X, H=0x%08X, L=0x%08X, HL=0x%08X, PC=0x%08X, %s", getA(), getB(), getC(), getD(), getE(), getH(), getL(), getHL(), pc, f.toString());
    }

//    public Registers incrementBC(){
//        setBC(getBC()+1);
//        return this;
//    }

    public Registers incrementHL(){
        setHL(getHL()+1);
        return this;
    }


}
