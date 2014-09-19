package nl.knaw.dans.common.lang;

import static org.hamcrest.core.IsSame.sameInstance;
import static org.junit.Assert.assertThat;
import nl.knaw.dans.common.lang.ALiasDelegate.AliasInterface;

import org.junit.Test;

public class AliasDelegateTest {

    private static enum DuplicateClass implements AliasInterface<Object> {
        one, two;

        @Override
        public Class<? extends Object> getAliasClass() {
            return String.class;
        }

        @Override
        public String getAlias() {
            return toString();
        }
    }

    private static enum DuplicateName implements AliasInterface<Object> {
        one(String.class), two(Integer.class);

        private final Class<? extends Object> aliasedClass;

        DuplicateName(final Class<? extends Object> bookmarkedClass) {
            this.aliasedClass = bookmarkedClass;

        }

        @Override
        public Class<? extends Object> getAliasClass() {
            return aliasedClass;
        }

        @Override
        public String getAlias() {
            return "name";
        }
    }

    private static enum Proper implements AliasInterface<Object> {
        one(String.class), two(Integer.class);

        private final Class<? extends Object> bookmarkedClass;

        Proper(final Class<? extends Object> bookmarkedClass) {
            this.bookmarkedClass = bookmarkedClass;

        }

        @Override
        public Class<? extends Object> getAliasClass() {
            return bookmarkedClass;
        }

        @Override
        public String getAlias() {
            return toString();
        }
    }

    @Test(expected = ALiasDelegate.InitializationException.class)
    public void duplicateClass() throws Exception {
        new ALiasDelegate<Object>(DuplicateClass.values());
    }

    @Test(expected = ALiasDelegate.InitializationException.class)
    public void duplicateName() throws Exception {
        new ALiasDelegate<Object>(DuplicateName.values());
    }

    @Test(expected = NullPointerException.class)
    public void noClass() throws Exception {
        new ALiasDelegate<Object>(Proper.values()).valueOf(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void wrongClass() throws Exception {
        new ALiasDelegate<Object>(Proper.values()).valueOf(Boolean.class);
    }

    @Test(expected = NullPointerException.class)
    public void noAlias() throws Exception {
        new ALiasDelegate<Object>(Proper.values()).valueOfAlias((String) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void wrongAlias() throws Exception {
        new ALiasDelegate<Object>(Proper.values()).valueOfAlias("");
    }

    @Test
    public void proper() throws Exception {
        final ALiasDelegate<Object> delegate = new ALiasDelegate<Object>(Proper.values());
        for (final AliasInterface<Object> value : Proper.values()) {
            assertThat(value, sameInstance(delegate.valueOfAlias(value.getAlias())));
            assertThat(value, sameInstance(delegate.valueOf(value.getAliasClass())));
        }
    }
}
