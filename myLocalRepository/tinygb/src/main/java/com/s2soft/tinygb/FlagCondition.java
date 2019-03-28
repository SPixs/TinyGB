package com.s2soft.tinygb;

public class FlagCondition {
	
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

