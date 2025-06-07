package mian.minecraft.ntm_ip.datagen;

import mian.minecraft.ntm_ip.NTMIP;
import mian.minecraft.ntm_ip.registry.IPMonitorFunctionRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraftforge.common.data.LanguageProvider;

public class NTMIPLangProvider extends LanguageProvider {
    public NTMIPLangProvider(DataGenerator gen) {
        super(gen.getPackOutput(), NTMIP.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        this.add(IPMonitorFunctionRegistry.ENABLE_BOTI.get().getConditionalText(true), "Enable BOTI: True");
        this.add(IPMonitorFunctionRegistry.ENABLE_BOTI.get().getConditionalText(false), "Enable BOTI: False");
    }

    public void add(Component component, String trans) {
        ComponentContents var4 = component.getContents();
        if (var4 instanceof TranslatableContents t) {
            this.add(t.getKey(), trans);
        } else {
            this.add(component.getString(), trans);
        }

    }
}
