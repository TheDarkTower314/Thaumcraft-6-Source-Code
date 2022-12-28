package thaumcraft.common.lib.utils;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;


public class BlockStateUtils
{
    public static EnumFacing getFacing(IBlockState state) {
        return EnumFacing.getFront(state.getBlock().getMetaFromState(state) & 0x7);
    }
    
    public static EnumFacing getFacing(int meta) {
        return EnumFacing.getFront(meta & 0x7);
    }
    
    public static boolean isEnabled(IBlockState state) {
        return (state.getBlock().getMetaFromState(state) & 0x8) != 0x8;
    }
    
    public static boolean isEnabled(int meta) {
        return (meta & 0x8) != 0x8;
    }
    
    public static IProperty getPropertyByName(IBlockState blockState, String propertyName) {
        for (IProperty property : blockState.getProperties().keySet()) {
            if (property.getName().equals(propertyName)) {
                return property;
            }
        }
        return null;
    }
    
    public static boolean isValidPropertyName(IBlockState blockState, String propertyName) {
        return getPropertyByName(blockState, propertyName) != null;
    }
    
    public static Comparable getPropertyValueByName(IBlockState blockState, IProperty<? extends Comparable> property, String valueName) {
        for (Comparable value : property.getAllowedValues()) {
            if (value.toString().equals(valueName)) {
                return value;
            }
        }
        return null;
    }
    
    public static ImmutableSet<IBlockState> getValidStatesForProperties(IBlockState baseState, IProperty... properties) {
        if (properties == null) {
            return null;
        }
        Set<IBlockState> validStates = Sets.newHashSet();
        PropertyIndexer propertyIndexer = new PropertyIndexer(properties);
        do {
            IBlockState currentState = baseState;
            for (IProperty property : properties) {
                IndexedProperty indexedProperty = propertyIndexer.getIndexedProperty(property);
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
        
        private PropertyIndexer(IProperty... properties) {
            indexedProperties = new HashMap<IProperty, IndexedProperty>();
            finalProperty = properties[properties.length - 1];
            IndexedProperty previousIndexedProperty = null;
            for (IProperty property : properties) {
                IndexedProperty indexedProperty = new IndexedProperty(property);
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
        
        public IndexedProperty getIndexedProperty(IProperty property) {
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
        
        private IndexedProperty(IProperty property) {
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
