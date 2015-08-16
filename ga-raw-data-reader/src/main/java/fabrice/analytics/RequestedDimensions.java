package fabrice.analytics;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import com.google.common.collect.UnmodifiableIterator;
import fabrice.domain.RowDefinition;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Created by fabrice on 14.08.15.
 */
public class RequestedDimensions {
    private final List<String> requestedDimensions;
    private final Collection<String> dimensionsWithoutHeaders;
    private final RowDefinition rowDefinition;
    private final int maxDimensionsInRequest;

    public RequestedDimensions(final RowDefinition rowDefinition, int maxDimensionsInRequest, String... requestedDimensions) {
        CollectionUtils.checkNoDuplicates(requestedDimensions);
        this.requestedDimensions = Arrays.asList(requestedDimensions);
        dimensionsWithoutHeaders = Collections2.filter(this.requestedDimensions, new Predicate<String>() {
            @Override
            public boolean apply(@Nullable String s) {
                return !rowDefinition.isIdHeader(s);
            }
        });
        this.rowDefinition = rowDefinition;
        this.maxDimensionsInRequest = maxDimensionsInRequest;
    }



    public Iterator<String> getPartitionedDimensions() {
// - arrange id dimensions at the befinning of the columns, in a deterministric order
// - detect duplicate dimension between id and asked dimension
        UnmodifiableIterator<List<String>> partition = Iterators.partition(dimensionsWithoutHeaders.iterator(), maxDimensionsInRequest - rowDefinition.getIdSize());
        Iterator<String> transform = Iterators.transform(partition, new Function<List<String>, String>() {
            @Nullable
            @Override
            public String apply(@Nullable List<String> strings) {
                ImmutableList.Builder<Object> builder = ImmutableList.builder();
                for (String header : rowDefinition.getIdHeaders()) {
                    builder.add(header);
                }
                ImmutableList<Object> list = builder.addAll(strings).build();
                return Joiner.on(",").join(list);
            }
        });
        return transform;
    }

    public RowDefinition getRowDefinition() {
        return rowDefinition;
    }
}
