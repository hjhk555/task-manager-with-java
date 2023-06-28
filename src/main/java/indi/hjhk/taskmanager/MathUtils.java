package indi.hjhk.taskmanager;

import javafx.scene.control.TextFormatter;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.function.UnaryOperator;

public class MathUtils {
    public static int parseAndCheckInt(String valName, String strVal, int min, int max) throws AlertMsgException{
        try{
            int intVal = Integer.parseInt(strVal);
            if (intVal < min || intVal > max)
                throw new AlertMsgException(String.format("'%s'越界，应在%d~%d内", valName, min, max));
            return intVal;
        }catch (NumberFormatException e){
            throw new AlertMsgException(String.format("'%s'解析失败，请输入整型数", valName));
        }
    }

    public static UnaryOperator<TextFormatter.Change> integerFilter = new UnaryOperator<TextFormatter.Change>() {
        @Override
        public TextFormatter.Change apply(TextFormatter.Change change) {
            if (change.getControlNewText().matches("-?[0-9]*"))
                return change;
            return null;
        }
    };

    public static byte code = 13;

    public static void writeEncodedString(String str, ObjectOutput out) throws IOException {
        byte[] bytes = str.getBytes();
        out.writeInt(bytes.length);
        for (byte b : bytes){
            out.writeByte(b+code);
        }
    }

    public static String readEncodeedString(ObjectInput in) throws IOException, ClassNotFoundException {
        int byteLength = in.readInt();
        byte[] bytes = new byte[byteLength];
        for (int i=0; i<byteLength; i++)
            bytes[i] = (byte) (in.readByte()-code);
        return new String(bytes);
    }
}
