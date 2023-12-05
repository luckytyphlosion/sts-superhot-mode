package superhot.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;
import javassist.expr.MethodCall;

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

    @SpireInstrumentPatch()
    public static ExprEditor scaleInGameTime()
    {
        return new ExprEditor() {
            @Override
            public void edit(MethodCall m) throws CannotCompileException {
                if (m.getMethodName().equals("getDeltaTime")) {
                    // scale up so that decimal portion isn't lost in saving
                    // will basically destroy any other use of play time
                    // which includes run history from the run history screen
                    // and total save file play time
                    m.replace("$_ = ($proceed($$) * 1000.0F);");
                }
            }
        };
    }
}
