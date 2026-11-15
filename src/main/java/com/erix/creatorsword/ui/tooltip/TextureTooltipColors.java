package com.erix.creatorsword.ui.tooltip;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import net.createmod.catnip.lang.FontHelper.Palette;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import java.io.IOException;
import java.util.*;
import java.util.stream.IntStream;

final class TextureTooltipColors {
    static int generation;
    private static final Map<Item, Palette> CACHE = new HashMap<>();
    static void reload() { CACHE.clear(); generation++; }
    static Palette get(Item item) { return CACHE.computeIfAbsent(item, TextureTooltipColors::load); }

    private static Palette load(Item item) {
        var id = BuiltInRegistries.ITEM.getKey(item);
        var path = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "textures/item/" + id.getPath() + ".png");
        try (var input = Minecraft.getInstance().getResourceManager().open(path);
             var image = NativeImage.read(input)) {
            Map<Integer, Integer> counts = new HashMap<>();
            for (int y = 0; y < image.getHeight(); y++) for (int x = 0; x < image.getWidth(); x++) {
                int abgr = image.getPixelRGBA(x, y);
                if ((abgr >>> 24) == 0) continue;
                int rgb = (abgr & 255) << 16 | (abgr & 0xff00) | (abgr >>> 16 & 255);
                counts.merge(rgb, 1, Integer::sum);
            }
            if (counts.isEmpty()) throw new IOException("No opaque pixels");
            var entries = new ArrayList<>(counts.entrySet());
            entries.sort(Map.Entry.<Integer,Integer>comparingByValue().reversed().thenComparingInt(Map.Entry::getKey));
            List<Group> groups = new ArrayList<>();
            for (var entry : entries) {
                double[] color = lab(entry.getKey());
                Group nearest = null;
                double best = .08;
                for (var group : groups) {
                    double d = distance(color, group.center());
                    if (d < best) { nearest = group; best = d; }
                }
                if (nearest == null) { nearest = new Group(); groups.add(nearest); }
                nearest.add(color, entry.getValue());
            }
            groups.sort(Comparator.comparingInt((Group g) -> g.count).reversed());
            int first = readable(groups.getFirst().center());
            int second = contrasting(first, groups.stream().skip(1)
                    .mapToInt(group -> readable(group.center())));
            if (distance(lab(first), lab(second)) < .20) {
                double[] source = lab(second);
                double hue = Math.atan2(source[2], source[1]);
                double chroma = Math.max(.16, Math.hypot(source[1], source[2]));
                second = contrasting(first, IntStream.range(0, 24).map(i -> {
                    double angle = hue + i * Math.PI / 12;
                    return readable(new double[]{.82, chroma*Math.cos(angle), chroma*Math.sin(angle)});
                }));
            }
            return new Palette(Style.EMPTY.withColor(second), Style.EMPTY.withColor(first));
        } catch (IOException e) {
            LogUtils.getLogger().warn("Unable to read template texture {}", path, e);
            return Palette.STANDARD_CREATE;
        }
    }

    private static int contrasting(int first, IntStream candidates) {
        double[] base = lab(first);
        int second = first;
        double greatest = -1;
        var iterator = candidates.iterator();
        while (iterator.hasNext()) {
            int candidate = iterator.nextInt();
            double difference = distance(base, lab(candidate));
            if (difference >= .20) return candidate;
            if (difference > greatest) { greatest = difference; second = candidate; }
        }
        return second;
    }

    private static class Group {
        int count;
        double[] sums = new double[3];
        void add(double[] c, int n) { count += n; for(int i=0;i<3;i++) sums[i]+=c[i]*n; }
        double[] center() { return new double[]{sums[0]/count,sums[1]/count,sums[2]/count}; }
    }
    private static double distance(double[] a,double[] b) {
        return Math.sqrt(Math.pow(a[0]-b[0],2)+Math.pow(a[1]-b[1],2)+Math.pow(a[2]-b[2],2));
    }
    private static double linear(int v) {
        double c=v/255.; return c<=.04045?c/12.92:Math.pow((c+.055)/1.055,2.4);
    }
    private static double[] lab(int rgb) {
        double r=linear(rgb>>16&255),g=linear(rgb>>8&255),b=linear(rgb&255);
        double l=Math.cbrt(.4122214708*r+.5363325363*g+.0514459929*b);
        double m=Math.cbrt(.2119034982*r+.6806995451*g+.1073969566*b);
        double s=Math.cbrt(.0883024619*r+.2817188376*g+.6299787005*b);
        return new double[]{.2104542553*l+.793617785*m-.0040720468*s,
            1.9779984951*l-2.428592205*m+.4505937099*s,.0259040371*l+.7827717662*m-.808675766*s};
    }
    private static int channel(double c) {
        c=Math.max(0,Math.min(1,c));return (int)Math.round(255*(c<=.0031308?12.92*c:1.055*Math.pow(c,1/2.4)-.055));
    }
    private static int readable(double[] c) {
        double light=Math.max(.72,Math.min(.90,c[0]));
        double l=Math.pow(light+.3963377774*c[1]+.2158037573*c[2],3);
        double m=Math.pow(light-.1055613458*c[1]-.0638541728*c[2],3);
        double s=Math.pow(light-.0894841775*c[1]-1.291485548*c[2],3);
        return channel(4.0767416621*l-3.3077115913*m+.2309699292*s)<<16
            |channel(-1.2684380046*l+2.6097574011*m-.3413193965*s)<<8
            |channel(-.0041960863*l-.7034186147*m+1.707614701*s);
    }
}
