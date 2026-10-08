package triangle.debug;

import triangle.abstractMachine.Machine;
import triangle.abstractSyntaxTrees.AbstractSyntaxTree;
import triangle.syntacticAnalyzer.SourcePosition;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Stack;

public class DebugPositionEmitter {
    String sourceName;
    Stack<AbstractSyntaxTree> stack = new Stack<>();
    SourcePosition[] positions = new SourcePosition[1024];
    int lastInstrAddr = -1;

    public DebugPositionEmitter(String sourceName) {
        this.sourceName = sourceName;
    }

    public void enter(AbstractSyntaxTree ast) {
        stack.push(ast);
    }

    public void exit() {
        stack.pop();
    }

    public void emit(int addr) {
        SourcePosition position;
        if (stack.isEmpty()) position = new SourcePosition();
        else position = stack.peek().getPosition();
        positions[addr] = position;
        lastInstrAddr = addr;
    }

    public void save(String objectFileName) {
        try (var objectFile = new FileOutputStream(objectFileName + ".dbg")) {
            var objectStream = new DataOutputStream(objectFile);

            objectStream.writeInt(sourceName.length());
            objectStream.writeBytes(sourceName);

            for (var addr = Machine.CB; addr <= lastInstrAddr; addr++) {
                var pos = positions[addr];

                objectStream.writeInt(pos.index);
                objectStream.writeInt(pos.length);
            }
        } catch (FileNotFoundException fnfe) {
            System.err.println("Error opening debug file: " + fnfe);
        } catch (IOException ioe) {
            System.err.println("Error writing debug file: " + ioe);
        }
    }
}