package superhot.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.ui.panels.TopPanel;

import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;

@SpirePatch(
    clz = TopPanel.class,
    method = "render"
)
public class TopPanelPatch {
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
