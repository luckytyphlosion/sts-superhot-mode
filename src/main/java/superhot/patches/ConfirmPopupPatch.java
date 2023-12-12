package superhot.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.audio.MusicMaster;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.saveAndContinue.SaveAndContinue;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import com.megacrit.cardcrawl.screens.options.ConfirmPopup;

import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

@SpirePatch(
    clz = ConfirmPopup.class,
    method = "yesButtonEffect"
)
public class ConfirmPopupPatch
{
    public static void saveGameTimeOnly()
    {
        SaveFile saveFile = SaveAndContinue.loadSaveFile(AbstractDungeon.player.chosenClass);
        saveFile.play_time = (long)CardCrawlGame.playtime;
        SaveAndContinue.save(saveFile);
        System.out.println("in end of saveGameTimeOnly");
    }

    @SpireInstrumentPatch()
    public static ExprEditor insertSaveCall()
    {
        return new ExprEditor() {
            @Override
            public void edit(MethodCall m) throws CannotCompileException {
                if (m.getMethodName().equals("fadeAll") && m.getClassName().equals(MusicMaster.class.getName())) {
                    System.out.println("insertSaveCall m.getClassName(): " + m.getClassName());
                    m.replace("{$_ = $proceed($$); " + ConfirmPopupPatch.class.getName() + ".saveGameTimeOnly();}");
                }
            }
        };
    }
}
