package triangle.abstractMachine;

public enum OpCode {
	/** 0x0 */ LOAD,
	/** 0x1 */ LOADA,
	/** 0x2 */ LOADI,
	/** 0x3 */ LOADL,
	/** 0x4 */ STORE,
	/** 0x5 */ STOREI,
	/** 0x6 */ CALL,
	/** 0x7 */ CALLI,
	/** 0x8 */ RETURN,
	/** 0x9 */ NOP,
	/** 0xA */ PUSH,
	/** 0xB */ POP,
	/** 0xC */ JUMP,
	/** 0xD */ JUMPI,
	/** 0xE */ JUMPIF,
	/** 0xF */ HALT
}
