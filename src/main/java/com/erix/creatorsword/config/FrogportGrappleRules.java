package com.erix.creatorsword.config;

import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class FrogportGrappleRules {

    private static final int MAX_LEVEL = 3;

    private final SelectorSet[] allowByLevel = new SelectorSet[MAX_LEVEL + 1];
    private final boolean[] othersLevel = new boolean[MAX_LEVEL + 1];
    private final SelectorSet[] allowUpTo = new SelectorSet[MAX_LEVEL + 1];
    private final SelectorSet[] mentionedAbove = new SelectorSet[MAX_LEVEL + 1];
    private final boolean[] othersActiveAt = new boolean[MAX_LEVEL + 1];
    private final SelectorSet deny = new SelectorSet();

    private FrogportGrappleRules() {
        for (int i = 0; i <= MAX_LEVEL; i++) {
            allowByLevel[i] = new SelectorSet();
            allowUpTo[i] = new SelectorSet();
            mentionedAbove[i] = new SelectorSet();
        }
    }

    public static FrogportGrappleRules fromConfig(
            RegistryAccess ra,
            List<? extends String> level0,
            List<? extends String> level1,
            List<? extends String> level2,
            List<? extends String> level3,
            List<? extends String> deny
    ) {
        FrogportGrappleRules r = new FrogportGrappleRules();

        r.loadLevel(ra, 0, level0);
        r.loadLevel(ra, 1, level1);
        r.loadLevel(ra, 2, level2);
        r.loadLevel(ra, 3, level3);

        if (deny != null && !deny.isEmpty()) {
            r.ingestSelectorsInto(ra, deny, r.deny);
        }

        r.allowUpTo[0] = new SelectorSet().addAll(r.allowByLevel[0]);
        for (int L = 1; L <= MAX_LEVEL; L++) {
            r.allowUpTo[L] = r.allowUpTo[L - 1].copy().addAll(r.allowByLevel[L]);
        }

        for (int L = 0; L <= MAX_LEVEL; L++) {
            SelectorSet above = new SelectorSet();
            for (int j = L + 1; j <= MAX_LEVEL; j++) {
                above.addAll(r.allowByLevel[j]);
            }
            r.mentionedAbove[L] = above;
        }

        boolean any = false;
        for (int L = 0; L <= MAX_LEVEL; L++) {
            any = any || r.othersLevel[L];
            r.othersActiveAt[L] = any;
        }

        return r;
    }

    public boolean canPull(LivingEntity target, int stickyLevel) {
        int L = clampLevel(stickyLevel);

        EntityType<?> type = target.getType();
        MobCategory cat = type.getCategory();

        if (deny.matches(type, cat)) return false;
        if (allowUpTo[L].matches(type, cat)) return true;
        if (othersActiveAt[L]) {
            return !mentionedAbove[L].matches(type, cat);
        }

        return false;
    }

    private void loadLevel(RegistryAccess ra, int level, List<? extends String> list) {
        if (list == null || list.isEmpty()) {
            othersLevel[level] = true;
            return;
        }

        ingestSelectorsInto(ra, list, allowByLevel[level]);
    }

    private static int clampLevel(int level) {
        if (level < 0) return 0;
        return Math.min(level, MAX_LEVEL);
    }

    private void ingestSelectorsInto(RegistryAccess ra, List<? extends String> selectors, SelectorSet out) {
        for (String raw : selectors) {
            ParsedSelector ps = ParsedSelector.parse(raw, ra);
            if (ps == null) continue;

            switch (ps.kind) {
                case CATEGORY -> out.allowCategories.add(ps.category);
                case TAG -> out.allowTags.add(ps.tag);
                case ENTITY -> out.allowEntities.add(ps.entityType);
            }
        }
    }

    private enum Kind { CATEGORY, TAG, ENTITY }

    private static final class SelectorSet {
        final EnumSet<MobCategory> allowCategories = EnumSet.noneOf(MobCategory.class);
        final Set<TagKey<EntityType<?>>> allowTags = new HashSet<>();
        final Set<EntityType<?>> allowEntities = new HashSet<>();

        boolean matches(EntityType<?> type, MobCategory cat) {
            if (allowCategories.contains(cat)) return true;
            if (allowEntities.contains(type)) return true;
            for (TagKey<EntityType<?>> t : allowTags) {
                if (type.is(t)) return true;
            }
            return false;
        }

        SelectorSet addAll(SelectorSet other) {
            this.allowCategories.addAll(other.allowCategories);
            this.allowTags.addAll(other.allowTags);
            this.allowEntities.addAll(other.allowEntities);
            return this;
        }

        SelectorSet copy() {
            SelectorSet c = new SelectorSet();
            c.addAll(this);
            return c;
        }
    }

    private record ParsedSelector(Kind kind, MobCategory category, TagKey<EntityType<?>> tag,
                                  EntityType<?> entityType) {

        static ParsedSelector parse(String raw, RegistryAccess ra) {
            if (raw == null) return null;
            String s = raw.trim();
            if (s.isEmpty()) return null;

            if (s.regionMatches(true, 0, "category:", 0, "category:".length())) {
                String name = s.substring("category:".length()).trim();
                if (name.isEmpty()) return null;
                try {
                    MobCategory cat = MobCategory.valueOf(name.toUpperCase());
                    return new ParsedSelector(Kind.CATEGORY, cat, null, null);
                } catch (IllegalArgumentException ignored) {
                    return null;
                }
            }

            if (s.startsWith("#")) {
                String idStr = s.substring(1).trim();
                ResourceLocation id = ResourceLocation.tryParse(idStr);
                if (id == null) return null;

                TagKey<EntityType<?>> tag = TagKey.create(Registries.ENTITY_TYPE, id);
                return new ParsedSelector(Kind.TAG, null, tag, null);
            }

            if (s.regionMatches(true, 0, "entity:", 0, "entity:".length())) {
                String idStr = s.substring("entity:".length()).trim();
                ResourceLocation id = ResourceLocation.tryParse(idStr);
                if (id == null) return null;

                var reg = ra.lookupOrThrow(Registries.ENTITY_TYPE);
                ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, id);
                var holder = reg.get(key).orElse(null);
                if (holder == null) return null;

                return new ParsedSelector(Kind.ENTITY, null, null, holder.value());
            }

            return null;
        }
    }
}