package indi.hjhk.taskmanager;

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
}
