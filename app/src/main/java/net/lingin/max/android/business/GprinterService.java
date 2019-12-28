package net.lingin.max.android.business;

import com.gprinter.command.EscCommand;
import com.gprinter.command.LabelCommand;

import net.lingin.max.android.net.model.GoodsPrintResult;
import net.lingin.max.android.service.BluetoothClientFactory;

import java.util.List;
import java.util.Vector;

import io.reactivex.Observable;

/**
 * @author Administrator
 * @date 2019/12/28 14:16
 */
public class GprinterService {

    private static GprinterService gprinterService = null;

    private int count = 0;

    public static GprinterService getInstance() {
        if (gprinterService == null) {
            return new GprinterService();
        }
        return gprinterService;
    }

    /**
     * 打印票据
     *
     * @param goodsPrintResultList 数据
     * @return 回调
     */
    public Observable<Integer> printBill(List<GoodsPrintResult> goodsPrintResultList) {
        GoodsPrintResult[] goodsPrintResults = goodsPrintResultList.toArray(new GoodsPrintResult[0]);
        return Observable.fromArray(goodsPrintResults)
                .map(this::initCommand);
    }

    private int initCommand(GoodsPrintResult goodsPrintResult) {
        EscCommand esc = new EscCommand();
        esc.addInitializePrinter();
        /* 设置打印居左 */
        esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);
        // 半切纸，这条命令只在行首有效
        esc.addCutPaper();
        // 加粗
        esc.addTurnEmphasizedModeOnOrOff(EscCommand.ENABLE.ON);
        // 加重
        esc.addTurnDoubleStrikeOnOrOff(EscCommand.ENABLE.ON);
        //  将当前打印位置设置到距离行首4mm处
        esc.addSetAbsolutePrintPosition((short) 32);
        // 品名
        esc.addText(goodsPrintResult.getItemName());
        // 打印并走纸3行
        esc.addPrintAndFeedLines((byte) 3);

        //  将当前打印位置设置到距离行首4mm处
        esc.addSetAbsolutePrintPosition((short) 32);
        // 编码
        esc.addText(goodsPrintResult.getItemNo());

        //  将当前打印位置设置到距离行首40mm处
        esc.addSetAbsolutePrintPosition((short) 320);
        // 单位
        esc.addText(goodsPrintResult.getUnitNo());
        // 打印并走纸3行
        esc.addPrintAndFeedLines((byte) 3);

        //  将当前打印位置设置到距离行首6mm处
        esc.addSetAbsolutePrintPosition((short) 48);
        // 零售价
        esc.addText(String.valueOf(goodsPrintResult.getSalePrice()));
        //  将当前打印位置设置到距离行首40mm处
        esc.addSetAbsolutePrintPosition((short) 320);
        // 规格
        esc.addText(goodsPrintResult.getItemSize());
        // 打印并换行
        esc.addPrintAndLineFeed();

        /* 开钱箱 */
        esc.addGeneratePlus(LabelCommand.FOOT.F5, (byte) 255, (byte) 255);
        // 打印并走纸3行
        esc.addPrintAndFeedLines((byte) 4);

        /* 加入查询打印机状态，用于连续打印 */
        byte[] bytes = {29, 114, 1};
        esc.addUserCommand(bytes);

        // 执行打印
        Vector<Byte> datas = esc.getCommand();
        BluetoothClientFactory.getClassicBluetoothClient().writeDataImmediately(datas);
        int result = count + 1;
        return count++;
    }
}
