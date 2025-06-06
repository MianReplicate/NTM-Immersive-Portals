package mian.minecraft.ntm_ip.helper;

import mian.minecraft.ntm_ip.NTMIP;
import net.minecraft.resources.ResourceLocation;

public class Helper {
    public static ResourceLocation createRL(String path){
        return ResourceLocation.fromNamespaceAndPath(NTMIP.MODID, path);
    }
}
