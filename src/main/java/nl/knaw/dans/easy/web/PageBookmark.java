package nl.knaw.dans.easy.web;

import nl.knaw.dans.common.lang.ALiasDelegate;
import nl.knaw.dans.common.lang.ALiasDelegate.AliasInterface;
import nl.knaw.dans.easy.web.admin.UserDetailsPage;
import nl.knaw.dans.easy.web.authn.ChangePasswordPage;
import nl.knaw.dans.easy.web.authn.login.LoginPage;
import nl.knaw.dans.easy.web.authn.RegistrationPage;
import nl.knaw.dans.easy.web.authn.RegistrationValidationPage;
import nl.knaw.dans.easy.web.deposit.DepositIntroPage;
import nl.knaw.dans.easy.web.doc.HelpPage;
import nl.knaw.dans.easy.web.migration.MigrationRedirectPage;
import nl.knaw.dans.easy.web.permission.PermissionReplyPrePage;
import nl.knaw.dans.easy.web.permission.PermissionRequestPage;
import nl.knaw.dans.easy.web.search.pages.AdvSearchPage;
import nl.knaw.dans.easy.web.search.pages.BrowsePage;
import nl.knaw.dans.easy.web.search.pages.MyDatasetsSearchResultPage;
import nl.knaw.dans.easy.web.search.pages.MyRequestsSearchResultPage;
import nl.knaw.dans.easy.web.view.dataset.DatasetViewPage;

import org.apache.wicket.Page;

/**
 * The instances are in fact entries of a cross reference between {@link Page} classes and URLs. A static
 * initializer guarantees a 1:1 relationship. The pages are automatically mounted by
 * {@link EasyWicketApplication}.
 * <p/>
 * Note that other bookmarkedNames exist within and without this application. F.i.:
 * <ul>
 *  <li>resource - Wicket resource</li>
 *  <li>rest - start url for rest services</li>
 *  <li>fedora* - fedora url if on same server as this application</li>
 *  <li>oai - oai url</li>
 *  <li>saxon - saxon url </li>
 *  <li>There may be others...</li>
 * </ul>
 */
public enum PageBookmark implements AliasInterface<Page>
{
    register(RegistrationPage.class, "register"), //
    registrationValidation(RegistrationValidationPage.class, "validate"), //
    login(LoginPage.class, "login"), //
    changePassword(ChangePasswordPage.class, "cp"), //
    home(HomePage.class, "home"), //
    browse(BrowsePage.class, "browse"), //
    error(ErrorPage.class, "error"), //
    advancedSearch(AdvSearchPage.class, "advancedsearch"), //
    myDataset(MyDatasetsSearchResultPage.class, "mydatasets"), //
    datasetView(DatasetViewPage.class, "datasets"), //
    permissionReply(PermissionReplyPrePage.class, "pmreply"), //
    permissionRequest(PermissionRequestPage.class, "pmrequest"), //
    deposit(DepositIntroPage.class, "deposit"), //
    myRequests(MyRequestsSearchResultPage.class, "myRequest"), //
    migration(MigrationRedirectPage.class, "dms"), //
    userDetailsPage(UserDetailsPage.class, "users"), //
    helpPage(HelpPage.class, "help");

    private static ALiasDelegate<Page> delegate = new ALiasDelegate<Page>(PageBookmark.values());
    private final String bookmarkedName;
    private final Class<? extends Page> bookmarkedClass;

    private PageBookmark(final Class<? extends Page> bookmarkedClass, final String bookmarkedName)
    {
        this.bookmarkedName = bookmarkedName;
        this.bookmarkedClass = bookmarkedClass;
    }

    public static PageBookmark valueOf(final Class<? extends Page> bookmarkedClass)
    {
        return (PageBookmark) delegate.valueOf(bookmarkedClass);
    }

    public static PageBookmark valueOfAlias(final String alias)
    {
        return (PageBookmark) delegate.valueOfAlias(alias);
    }

    @Override
    public Class<? extends Page> getAliasClass()
    {
        return bookmarkedClass;
    }

    @Override
    public String getAlias()
    {
        return bookmarkedName;
    }
}
