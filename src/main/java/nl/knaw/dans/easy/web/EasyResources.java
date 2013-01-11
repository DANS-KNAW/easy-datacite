package nl.knaw.dans.easy.web;

/**
 * Keys for common resources, found on EasyWicketApplication.properties. These resources can be found throughout the application and have a uniform translation.
 * Please do not use common resources for resources that are specific to a particular package or a particular page.
 * 
 * @author ecco Mar 13, 2009
 * @author lobo (refactored)
 */
public interface EasyResources
{
    String DATETIME_FORMAY_KEY = "DateAndTimeFormat";
    String DATE_FORMAY_KEY = "DateFormat";

    String DATASET_UNAVAILABLE = "main.dataset_unavailable";
    String DATASET_LOAD = "main.dataset_load_failure";
    String DATASET_UNDERCONSTRUCTION = "main.dataset_under_construction";

    String DEFAULT_ERROR_MESSAGE = "default_error_message";

    String SHORTENEDSTRINGMODEL_DEFAULT_SHORTENSTRING = "shortenedstringmodel.default.shortenstring";
    String SHORTENEDSTRINGMODEL_DEFAULT_SHORTENCHARCOUNT = "shortenedstringmodel.default.shortencharcount";

    String ADVSEARCH_CRITERIUM_PREFIX = "advsearch.criterium.prefix";
    String ADVSEARCH_ANYFIELD_CRITERIUM_PREFIX = "advsearch.anyfield.criterium.prefix";

    String USER_USER_ID = "user.userId";
    String USER_PASSWORD = "user.password";
    String USER_CONFIRM_PASSWORD = "user.confirmPassword";
    String USER_EMAIL = "user.email";
    String USER_TITLE = "user.title";
    String USER_INITIALS = "user.initials";
    String USER_PREFIXES = "user.prefixes";
    String USER_SURNAME = "user.surname";
    String USER_DISPLAYNAME = "user.displayName";
    String USER_ORGANIZATION = "user.organization";
    String USER_DEPARTMENT = "user.department";
    String USER_FUNCTION = "user.function";
    String USER_ADDRESS = "user.address";
    String USER_POSTALCODE = "user.postalCode";
    String USER_CITY = "user.city";
    String USER_COUNTRY = "user.country";
    String USER_TELEPHONE = "user.telephone";
    String USER_DISCIPLINE = "user.disciplines";
    String USER_DAI = "user.dai";
    String USER_STATE = "user.state";
    String USER_ROLES = "user.roles";

    String USER_WELCOME = "user_welcome";

    String UPDATE_BUTTON = "update";
    String CANCEL_BUTTON = "cancel";
    String REQUEST_BUTTON = "request";
    String SUBMIT_BUTTON = "submit";

    String ADD_LINK = "addLink";
    String EDIT_LINK = "editLink";
    String DONE_LINK = "doneLink";
    String CANCEL_LINK = "cancelLink";

    String DEFAULT_FORM_ERROR = "default_form_error";
    String INSUFFICIENT_PARAMETERS = "insufficient_parameters";
    String INTERNAL_ERROR = "internal_error";
    String NR_ERRORS_IN_FORM = "nr_errors_in_form";
    String ILLEGAL_ACCESS = "illegal_visit";
    String ILLEGAL_PAGE_PARAMETERS = "illegal_parameters";
    String INVALID_URL = "invalid_url";

    String COULD_NOT_RETRIEVE_ARCHIS_FILE = "admin.could_not_retrieve_archis_file";
    String COULD_NOT_RETRIEVE_USERS = "could_not_retrieve_users";
    String DEPOSIT_UNSAVED_CHANGES = "deposit.unsaved_changes";
    String DEPOSIT_COMPLETED = "deposit.completed";
    String DATASET_SUBMISSION = "deposit.dataset_submisssion";
    String DATASET_METADATA_CHANGED = "deposit.metadata_changed";
    String DATASET_FAIL_TO_SAVE = "deposit.metadata_fail_to_save";
    String DATASET_SAVED = "deposit.dataset_saved";
    String USER_NOT_FOUND = "user_not_found";
    String REGISTRATION_COMPLETE = "authn.registration_complete";
    String REGISTRATION_ACCOUNT_VALIDATED = "authn.validation_succesful";
    String REGISTRATION_ACCOUNT_VALIDATED_TITLE = "authn.validation_succesful.infoPageTitle";
    String ANONYMOUS_USER = "authn.anonymous_user";
    String URL_AUTHENTICATION = "authn.url_authentication";
    String URL_PARAMETERS = "authn.url_parameters";
    String USER_UPDATE_FAILED = "authn.user_update_failed";
    String LICENSE_COMPOSER = "deposit.license_composer";
    String LICENSE_DATASET = "deposit.license_dataset";
    String SUCCESFUL_UPDATE = "succesful_update";
    String USER_UPDATE_ERROR = "admin.user_update_error";
    String ADD_NOT_IMPLEMENTED = "admin.add_not_implemented";
    String ERROR_IN_GETTING_GROUPS = "admin.error_in_getting_groups";
    String FORM_INVALID_PARAMETERS = "invalid_form";
    String PASSWORD_SUCCESFULLY_CHANGED = "changePassword.state.PasswordChanged";
    String ERROR_GETTING_ARCHIVISTS_LIST = "error_getting_archivists_list";
    String SEARCH_FAILURE = "search.search_failure";
    String BROWSE_SEARCH_FAILURE = "browse.search_failure";
    String DEPOSIT_APPLICATION_ERROR = "deposit.system_error";
    String DATASET_CREATION = "deposit.dataset_creation";
    String DATASET_RETRIEVAL = "deposit.dataset_retrieval";
    String DISCIPLINE_RETRIEVAL = "deposit.discipline_retrieval";
    String PERMISSION_SUBMIT_FAIL = "permission.submit_failure";
    String PERMISSION_SUBMITTED = "permission.reply.submitted";
    String PERMISSION_REQUEST_FAIL = "permission.request_failure";
    String PERMISSION_REQUESTED = "permission.request.submitted";
    String FE_BAD_REQUEST = "template.fileexplorer.bad_request";
    String FE_SERVER_ERROR = "template.fileexplorer.server_error";
    String FE_COULD_NOT_COMPLETE = "template.fileexplorer.not_complete";
    String FE_SECURITY_ERROR = "template.fileexplorer.security";
    String FE_PLEASE_LOGIN = "template.fileexplorer.login";
    String SHOW_RESULTS = "template.search.show_results";
    String LOAD_DATASET = "view.load_dataset";
    String ERROR_RETRIEVING_DOWNLOAD_HISTORY = "view.download_history";
    String SAVE_WORKFLOW = "view.save_workflow";
    String UNSUBMIT_DATASET = "view.unsubmit_dataset";
    String NO_PUBLISHED_DATAFILES = "view.no_published_datafiles";
    String WORKFLOW_NOT_COMPLETED = "view.workflow_not_completed";
    String UNABLE_TO_PUBLISH = "view.unable_to_publish";
    String UNABLE_TO_UNPUBLISH = "view.unable_to_unpublish";
    String UNABLE_TO_MAINTAIN_DATASET = "view.unable_to_maintain_dataset";
    String UNABLE_TO_REPUBLISH_DATASET = "view.unable_to_republish_dataset";
    String UNABLE_TO_CHANGE_DEPOSITOR = "view.unable_to_change_depositor";
    String DATASET_STATUS_CHANGED = "dataset_status_changed";
    String ERROR_SAVING_ADMINISTRATIVE_METADATA = "view.save_administrative_metadata";
    String ERROR_DELETING_DATASET = "view.delete_dataset";
    String ERROR_RESTORING_DATASET = "view.restore_dataset";
    String NO_USER_SELECTED = "wicket.no_user_selected";
    String NONVALID_USER = "wicket.nonvalid_user";
    String ERROR_LOADING_MODEL_OBJECT = "view.load_model_object";
    String ERROR_CLONING_DATASET = "view.clone_dataset";
    String PLEASE_ENTER_A_SEARCH_VALUE = "advancedsearch.nothing_filled_in";

    String NOT_FOUND = "not_found";
    String USE_PERSITENT_IDENTIFIER = "use.persistent.identifier";

    String VALID_TEMPLATE = "valid_template";
    String INVALID_TEMPLATE = "invalid_template";
    String SAVED_TEMPLATE = "saved_template";
    String NOT_SAVED_TEMPLATE = "not_saved_template";
}
