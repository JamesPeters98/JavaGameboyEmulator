package com.jamesdpeters.cpu;

public class FlagsRegister implements Cloneable{

    public boolean ZERO;
    public boolean SUBTRACT;
    public boolean HALF_CARRY;
    public boolean CARRY;

    private final static int ZERO_FLAG_BYTE_POS = 7;
    private final static int SUBTRACT_FLAG_BYTE_POS = 6;
    private final static int HALF_CARRY_FLAG_BYTE_POS = 5;
    private final static int CARRY_FLAG_BYTE_POS = 4;

    public FlagsRegister(boolean zero, boolean subtract, boolean half_carry, boolean carry){
        this.ZERO = zero;
        this.SUBTRACT = subtract;
        this.HALF_CARRY = half_carry;
        this.CARRY = carry;
    }

    public FlagsRegister(){
        this(false,false,false,false);
    }

    int get(){
        return (ZERO ? 1 : 0) << ZERO_FLAG_BYTE_POS |
                (SUBTRACT ? 1 : 0) << SUBTRACT_FLAG_BYTE_POS |
                (HALF_CARRY ? 1 : 0) << HALF_CARRY_FLAG_BYTE_POS |
                (CARRY ? 1 : 0) << CARRY_FLAG_BYTE_POS;
    }

    void setFlag(int byte_){
        ZERO = ((byte_ >> ZERO_FLAG_BYTE_POS) & 0b1) != 0;
        SUBTRACT = ((byte_ >> SUBTRACT_FLAG_BYTE_POS) & 0b1) != 0;
        HALF_CARRY = ((byte_ >> HALF_CARRY_FLAG_BYTE_POS) & 0b1) != 0;
        CARRY = ((byte_ >> CARRY_FLAG_BYTE_POS) & 0b1) != 0;
    }

    @Override
    public String toString() {
        return String.valueOf(ZERO ? 'Z' : '-') +
                (SUBTRACT ? 'N' : '-') +
                (HALF_CARRY ? 'H' : '-') +
                (CARRY ? 'C' : '-') +
                "----";
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
