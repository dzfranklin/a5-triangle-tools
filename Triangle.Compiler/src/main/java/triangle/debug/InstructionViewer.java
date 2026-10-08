package triangle.debug;

import triangle.abstractMachine.Instruction;
import triangle.syntacticAnalyzer.SourcePosition;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HexFormat;

public class InstructionViewer {
    static String objectName;
    static String sourceName;

    static byte[] sourceBytes;
    static ArrayList<Instruction> instructions = new ArrayList<>();
    static ArrayList<SourcePosition> positions = new ArrayList<>();

    static DataInputStream objectStream;
    static DataInputStream debugStream;

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: filename.obj");
            System.exit(1);
        }

        objectName = args[0];

        try {
            parse();
            display();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

public static void parse() throws IOException {
        var objectFile = new FileInputStream(objectName);
        objectStream = new DataInputStream(objectFile);

        var debugFile = new FileInputStream(objectName + ".dbg");
        debugStream = new DataInputStream(debugFile);

        int sourceNameLen = debugStream.readInt();
        byte[] sourceNameBytes = new byte[sourceNameLen];
        int nameBytesRead = debugStream.read(sourceNameBytes);
        if (nameBytesRead != sourceNameLen) throw new RuntimeException("bad debug file");
        sourceName = new String(sourceNameBytes, StandardCharsets.UTF_8);

        var sourceFile = new FileInputStream(sourceName);
        sourceBytes = sourceFile.readAllBytes();

        while (true) {
            var ins = Instruction.read(objectStream);
            if (ins == null) break;

            var pos = new SourcePosition();
            pos.index = debugStream.readInt();
            pos.length = debugStream.readInt();

            instructions.add(ins);
            positions.add(pos);
        }
    }

    public static void display() throws IOException {
        var hexFormat = HexFormat.of().withUpperCase();

        System.out.println("     op     r   n         d");

        for (int i = 0; i < instructions.size(); i++) {
            var ins = instructions.get(i);
            var pos = positions.get(i);
            var source = resolveSourceSubstring(pos);

            System.out.print(hexFormat.toHexDigits(i, 2));
            System.out.print("  ");
            System.out.printf("%-6s  %s  %-8X  %-8X", ins.getOpCode(), ins.getRegister(), ins.getLength(), ins.getOperand());
            System.out.print(" ");
            System.out.print(" [");
            printSource(source);
            System.out.print("]\n");
        }
    }

    public static void printSource(String source) {
        int maxLen = 75;
        String elip = " ... ";

        String display = source.replaceAll("\n", " \\\\n ").replaceAll("  +", " ");

        if (display.length() > maxLen) {
            int leftLen = (maxLen - elip.length()) / 2;
            display = display.substring(0, leftLen) + elip + display.substring(display.length() - leftLen);
        }

        System.out.print(display);
    }

    public static String resolveSourceSubstring(SourcePosition pos) {
        var bytes = Arrays.copyOfRange(sourceBytes, pos.index, pos.index + pos.length);
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
