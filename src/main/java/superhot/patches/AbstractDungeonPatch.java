package superhot.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;

@SpirePatch(
    clz = AbstractDungeon.class,
    method = "update"
)
public class AbstractDungeonPatch {
    @SpireInstrumentPatch()
    public static ExprEditor modifyCondition()
    {
        return new ExprEditor() {
            @Override
            public void edit(FieldAccess f) throws CannotCompileException {
                if (f.getFieldName().equals("stopClock") && f.getClassName().equals(CardCrawlGame.class.getName())) {
                    String abstractDungeonClassName = AbstractDungeon.class.getName();
                    f.replace(String.format(
                            "$_ = $proceed($$) || %s.actionManager.actions.isEmpty() || %s.screen != %s.CurrentScreen.NONE;",
                            abstractDungeonClassName,
                            abstractDungeonClassName,
                            abstractDungeonClassName
                        )
                    );
                }
            }
        };
    }
}
