package net.hallowed.armortweaks;

import com.google.common.collect.Multimap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = ArmorTweaks.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ArmorAttributeOverrides {

    @SubscribeEvent
    public static void onItemAttrs(ItemAttributeModifierEvent event) {
        ItemStack stack = event.getItemStack();
        Item item = stack.getItem();
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(item);
        if (id == null) return;

        ArmorOverrides.get(id).ifPresent(pair -> {
            replaceAttribute(event, Attributes.ARMOR, pair.armor(), id);
            replaceAttribute(event, Attributes.ARMOR_TOUGHNESS, pair.toughness(), id);
        });
    }

    private static void replaceAttribute(ItemAttributeModifierEvent event, Attribute attr, double newAmount, ResourceLocation itemId) {
        Multimap<Attribute, AttributeModifier> mods = event.getModifiers();

        Collection<AttributeModifier> current = new ArrayList<>(mods.get(attr));
        if (current.isEmpty()) return;

        for (AttributeModifier old : current) {
            event.removeModifier(attr, old);
        }

        UUID uuid = deterministicUuid(itemId, attr);
        AttributeModifier add = new AttributeModifier(
                uuid,
                "armortweaks_override_" + attr.getDescriptionId(),
                newAmount,
                AttributeModifier.Operation.ADDITION
        );
        event.addModifier(attr, add);
    }

    private static UUID deterministicUuid(ResourceLocation id, Attribute attr) {
        String key = id + "|" + attr.getDescriptionId() + "|armortweaks";
        return UUID.nameUUIDFromBytes(key.getBytes());
    }
}
