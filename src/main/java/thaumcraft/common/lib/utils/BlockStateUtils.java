// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.lib.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Collection;
import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.UnmodifiableIterator;
import net.minecraft.block.properties.IProperty;
import net.minecraft.util.EnumFacing;
import net.minecraft.block.state.IBlockState;

public class BlockStateUtils
{
    public static EnumFacing getFacing(final IBlockState state) {
        return EnumFacing.getFront(state.getBlock().getMetaFromState(state) & 0x7);
    }
    
    public static EnumFacing getFacing(final int meta) {
        return EnumFacing.getFront(meta & 0x7);
    }
    
    public static boolean isEnabled(final IBlockState state) {
        return (state.getBlock().getMetaFromState(state) & 0x8) != 0x8;
    }
    
    public static boolean isEnabled(final int meta) {
        return (meta & 0x8) != 0x8;
    }
    
    public static IProperty getPropertyByName(final IBlockState blockState, final String propertyName) {
        for (final IProperty property : blockState.getProperties().keySet()) {
            if (property.getName().equals(propertyName)) {
                return property;
            }
        }
        return null;
    }
    
    public static boolean isValidPropertyName(final IBlockState blockState, final String propertyName) {
        return getPropertyByName(blockState, propertyName) != null;
    }
    
    public static Comparable getPropertyValueByName(final IBlockState blockState, final IProperty<? extends Comparable> property, final String valueName) {
        for (final Comparable value : property.getAllowedValues()) {
            if (value.toString().equals(valueName)) {
                return value;
            }
        }
        return null;
    }
    
    public static ImmutableSet<IBlockState> getValidStatesForProperties(final IBlockState baseState, final IProperty... properties) {
        if (properties == null) {
            return null;
        }
        final Set<IBlockState> validStates = Sets.newHashSet();
        final PropertyIndexer propertyIndexer = new PropertyIndexer(properties);
        do {
            IBlockState currentState = baseState;
            for (final IProperty property : properties) {
                final IndexedProperty indexedProperty = propertyIndexer.getIndexedProperty(property);
                currentState = currentState.withProperty(property, indexedProperty.getCurrentValue());
            }
            validStates.add(currentState);
        } while (propertyIndexer.increment());
        return (ImmutableSet<IBlockState>)ImmutableSet.copyOf((Collection)validStates);
    }
    
    private static class PropertyIndexer
    {
        private HashMap<IProperty, IndexedProperty> indexedProperties;
        private IProperty finalProperty;
        
        private PropertyIndexer(final IProperty... properties) {
            indexedProperties = new HashMap<IProperty, IndexedProperty>();
            finalProperty = properties[properties.length - 1];
            IndexedProperty previousIndexedProperty = null;
            for (final IProperty property : properties) {
                final IndexedProperty indexedProperty = new IndexedProperty(property);
                if (previousIndexedProperty != null) {
                    indexedProperty.parent = previousIndexedProperty;
                    previousIndexedProperty.child = indexedProperty;
                }
                indexedProperties.put(property, indexedProperty);
                previousIndexedProperty = indexedProperty;
            }
        }
        
        public boolean increment() {
            return indexedProperties.get(finalProperty).increment();
        }
        
        public IndexedProperty getIndexedProperty(final IProperty property) {
            return indexedProperties.get(property);
        }
    }
    
    private static class IndexedProperty
    {
        private ArrayList<Comparable> validValues;
        private int maxCount;
        private int counter;
        private IndexedProperty parent;
        private IndexedProperty child;
        
        private IndexedProperty(final IProperty property) {
            (validValues = new ArrayList<Comparable>()).addAll(property.getAllowedValues());
            maxCount = validValues.size() - 1;
        }
        
        public boolean increment() {
            if (counter < maxCount) {
                ++counter;
                return true;
            }
            if (hasParent()) {
                resetSelfAndChildren();
                return parent.increment();
            }
            return false;
        }
        
        public void resetSelfAndChildren() {
            counter = 0;
            if (hasChild()) {
                child.resetSelfAndChildren();
            }
        }
        
        public boolean hasParent() {
            return parent != null;
        }
        
        public boolean hasChild() {
            return child != null;
        }
        
        public int getCounter() {
            return counter;
        }
        
        public int getMaxCount() {
            return maxCount;
        }
        
        public Comparable getCurrentValue() {
            return validValues.get(counter);
        }
    }
}
