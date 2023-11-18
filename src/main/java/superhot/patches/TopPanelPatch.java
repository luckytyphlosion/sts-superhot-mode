package superhot.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.stats.CharStat;
import com.megacrit.cardcrawl.ui.panels.TopPanel;

import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;
import javassist.expr.MethodCall;

@SpirePatch(
    clz = TopPanel.class,
    method = "render"
)
public class TopPanelPatch {
    public static String formatHMSM_withMillis(float t) {
        String res = "";
        long duration = (long)t;
        double seconds = t % 60;
        duration /= 60L;
        int minutes = (int)(duration % 60L);
        int hours = (int)t / 3600;

        if (hours > 0) {
            res = String.format("%02d:%02d:%.03f", hours, minutes, seconds);
        } else {
            res = String.format("%02d:%.03f", minutes, seconds);
        } 
        return res;
    }

    @SpireInstrumentPatch()
    public static ExprEditor replaceFormatCalls()
    {
        return new ExprEditor() {
            @Override
            public void edit(MethodCall m) throws CannotCompileException {
                if (m.getMethodName().equals("formatHMSM") && m.getClassName().equals(CharStat.class.getName())) {
                    m.replace("$_ = " + TopPanelPatch.class.getName() + ".formatHMSM_withMillis($1);");
                }
            }
        };
    }

    @SpireInstrumentPatch()
    public static ExprEditor modifyCondition()
    {
        return new ExprEditor() {
            @Override
            public void edit(FieldAccess f) throws CannotCompileException {
                if (f.getFieldName().equals("screen") && f.getClassName().equals(AbstractDungeon.class.getName())) {
                    f.replace("$_ = " + AbstractDungeon.class.getName() + ".CurrentScreen.MAP;");
                }
            }
        };
    }
}
