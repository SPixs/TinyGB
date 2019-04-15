package com.s2soft.tinygb.cpu;

public class FlagCondition {
	
	public final static FlagCondition Z = new FlagCondition(FlagField.Z, true);
	public final static FlagCondition NZ = new FlagCondition(FlagField.Z, false);
	public final static FlagCondition N = new FlagCondition(FlagField.N, true);
	public final static FlagCondition NN = new FlagCondition(FlagField.N, false);
	public final static FlagCondition H = new FlagCondition(FlagField.H, true);
	public final static FlagCondition NH = new FlagCondition(FlagField.H, false);
	public final static FlagCondition C = new FlagCondition(FlagField.C, true);
	public final static FlagCondition NC = new FlagCondition(FlagField.C, false);
	
	public enum FlagField {
		Z {	boolean getStatus(GBCpu cpu) { return cpu.getFlagZero(); } },
		N {	boolean getStatus(GBCpu cpu) { return cpu.getFlagSubtract(); } },
		H {	boolean getStatus(GBCpu cpu) { return cpu.getFlagHalfCarry(); } },
		C {	boolean getStatus(GBCpu cpu) { return cpu.getFlagCarry(); } };
		
		abstract boolean getStatus(GBCpu cpu);
	}

	private FlagField m_field;
	private boolean m_expectedState;
	
	public FlagCondition(FlagField field, boolean expectedState) {
		m_field = field;
		m_expectedState = expectedState;
	}

	public boolean evaluate(GBCpu cpu) {
		return m_field.getStatus(cpu) == m_expectedState;
	}

	public String asText() {
		return (m_expectedState ? "" : "N") + m_field.name();
	}
	
}

