package nl.knaw.dans.easy.web;

import static org.hamcrest.core.IsSame.sameInstance;
import static org.junit.Assert.assertThat;

import org.apache.wicket.Page;
import org.apache.wicket.Resource;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.WebResource;
import org.junit.Test;

public class DelegatedBookmarkTest {
    @Test(expected = IllegalArgumentException.class)
    public void wrongPageUrl() {
        PageBookmark.valueOfAlias("blablarabarbera");
    }

    @Test(expected = IllegalArgumentException.class)
    public void wrongResourceUrl() {
        ResourceBookmark.valueOfAlias("blablarabarbera");
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyPageUrl() {
        PageBookmark.valueOfAlias("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyResourceUrl() {
        ResourceBookmark.valueOfAlias("");
    }

    @Test(expected = NullPointerException.class)
    public void noPageUrl() {
        PageBookmark.valueOfAlias(null);
    }

    @Test(expected = NullPointerException.class)
    public void noResourceUrl() {
        ResourceBookmark.valueOfAlias(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void wrongPageClass() {
        PageBookmark.valueOf(WebPage.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void wrongResourceClass() {
        ResourceBookmark.valueOf(WebResource.class);
    }

    @Test(expected = NullPointerException.class)
    public void noPageClass() {
        final Class<? extends Page> page = null;
        PageBookmark.valueOf(page);
    }

    @Test(expected = NullPointerException.class)
    public void noResourceClass() {
        final Class<? extends Resource> resource = null;
        ResourceBookmark.valueOf(resource);
    }

    @Test
    public void allTheRest() throws Exception {
        for (final PageBookmark value : PageBookmark.values()) {
            assertThat(value, sameInstance(PageBookmark.valueOfAlias(value.getAlias())));
            assertThat(value, sameInstance(PageBookmark.valueOf(value.getAliasClass())));
        }
        for (final ResourceBookmark value : ResourceBookmark.values()) {
            assertThat(value, sameInstance(ResourceBookmark.valueOfAlias(value.getAlias())));
            assertThat(value, sameInstance(ResourceBookmark.valueOf(value.getAliasClass())));
            ResourceBookmark.getResourceReferenceOf(value.getAliasClass());
        }
    }
}
