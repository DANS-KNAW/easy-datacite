package nl.knaw.dans.easy.domain.user;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import nl.knaw.dans.common.lang.user.Person;
import nl.knaw.dans.common.lang.user.User;
import nl.knaw.dans.easy.domain.exceptions.AnonymousUserException;
import nl.knaw.dans.easy.domain.model.user.CreatorRole;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.model.user.Group;

import org.joda.time.DateTime;

public class EasyUserAnonymous implements EasyUser {
    private static final long serialVersionUID = 1L;

    private static EasyUserAnonymous instance = new EasyUserAnonymous();

    public static EasyUserAnonymous getInstance() {
        return instance;
    }

    public void addGroupId(String groupId) {
        throw new AnonymousUserException("Cannot add group id " + groupId + " to an anonymous user.");
    }

    public void addRole(Role role) {
        throw new AnonymousUserException("Cannot add role " + role.toString() + " to an anonymous user.");
    }

    public CreatorRole getCreatorRole() {
        throw new AnonymousUserException("Anonymous user does not have a creator role");
    }

    public Set<String> getGroupIds() {
        return Collections.emptySet();
    }

    public Set<Group> getGroups() {
        return Collections.emptySet();
    }

    public Set<Role> getRoles() {
        return Collections.emptySet();
    }

    public boolean hasRole(Role... roles) {
        return false;
    }

    public boolean isMemberOf(Group... groups) {
        return false;
    }

    public boolean isMemberOfGroup(Collection<String> groupIds) {
        return false;
    }

    public void joinGroup(Group group) {
        throw new AnonymousUserException("Anonymous user cannot join group " + group.toString() + ".");
    }

    public boolean leaveGroup(Group group) {
        throw new AnonymousUserException("Anonymous user cannot leave group " + group.toString() + ", because it can never be part of a group.");
    }

    public boolean removeRole(Role role) {
        throw new AnonymousUserException("Anonymous user does not have a role and can therefore not remove the role " + role.toString() + ".");
    }

    public void synchronizeOn(EasyUser otherUser) {
        throw new AnonymousUserException("Cannot synchronize anonymous user.");
    }

    public boolean getAcceptConditionsOfUse() {
        return false;
    }

    public DateTime getLastLoginDate() {
        throw new AnonymousUserException("Cannot get last login date of an anonymous user.");
    }

    public String getPassword() {
        throw new AnonymousUserException("Cannot get password of an anonymous user.");
    }

    public String getSHAEncryptedPassword() {
        throw new AnonymousUserException("Cannot get SHA encrypted password of an anonymous user.");
    }

    public State getState() {
        throw new AnonymousUserException("Cannot get state of an anonymous user.");
    }

    public boolean isActive() {
        return true;
    }

    public boolean isBlocked() {
        return false;
    }

    public boolean isFirstLogin() {
        return false;
    }

    public boolean isQualified() {
        return false;
    }

    public boolean isUserInfoUpdateRequired() {
        return false;
    }

    public boolean getOptsForNewsletter() {
        return false;
    }

    public void setOptsForNewsletter(boolean optsForNewsletter) {
        throw new AnonymousUserException("An anonymous user cannot opt for a newsletter.");
    }

    public void setAcceptConditionsOfUse(boolean accept) {
        throw new AnonymousUserException("Cannot set accept conditions of use an anonymous user.");
    }

    public void setId(String userId) {
        throw new AnonymousUserException("Cannot set id of use an anonymous user.");
    }

    public void setPassword(String password) {
        throw new AnonymousUserException("Cannot set password of use an anonymous user.");
    }

    public void setSHAEncryptedPassword(String encryptedPassword) {
        throw new AnonymousUserException("Cannot set SHA encrypted password of use an anonymous user.");
    }

    public void setState(State state) {
        throw new AnonymousUserException("Cannot set state of use an anonymous user.");
    }

    public void synchronizeOn(User otherUser) {
        throw new AnonymousUserException("Cannot syncrhonize on an anonymous user.");
    }

    public String getAddress() {
        throw new AnonymousUserException("Cannot get address of an anonymous user.");
    }

    public String getCity() {
        throw new AnonymousUserException("Cannot get city of an anonymous user.");
    }

    public String getCommonName() {
        return "Anonymous";
    }

    public String getCountry() {
        throw new AnonymousUserException("Cannot get country of an anonymous user.");
    }

    public String getDepartment() {
        throw new AnonymousUserException("Cannot get department of an anonymous user.");
    }

    public String getDisplayName() {
        return "Anonymous";
    }

    public String getEmail() {
        throw new AnonymousUserException("Cannot get email of an anonymous user.");
    }

    public String getFirstname() {
        throw new AnonymousUserException("Cannot get firstname of an anonymous user.");
    }

    public String getFunction() {
        throw new AnonymousUserException("Cannot get function of an anonymous user.");
    }

    public String getInitials() {
        throw new AnonymousUserException("Cannot get initials of an anonymous user.");
    }

    public String getOrganization() {
        throw new AnonymousUserException("Cannot get organisation of an anonymous user.");
    }

    public String getPostalCode() {
        throw new AnonymousUserException("Cannot get postal code of an anonymous user.");
    }

    public String getPrefixes() {
        throw new AnonymousUserException("Cannot get prefixes of an anonymous user.");
    }

    public String getSurname() {
        throw new AnonymousUserException("Cannot get surname of an anonymous user.");
    }

    public String getTelephone() {
        throw new AnonymousUserException("Cannot get telephone of an anonymous user.");
    }

    public String getTitle() {
        throw new AnonymousUserException("Cannot get title of an anonymous user.");
    }

    public void setAddress(String address) {
        throw new AnonymousUserException("Cannot set address of an anonymous user.");
    }

    public void setCity(String city) {
        throw new AnonymousUserException("Cannot set city of an anonymous user.");
    }

    public void setCountry(String country) {
        throw new AnonymousUserException("Cannot set country of an anonymous user.");
    }

    public void setDepartment(String department) {
        throw new AnonymousUserException("Cannot set department of an anonymous user.");
    }

    public void setEmail(String email) {
        throw new AnonymousUserException("Cannot set email of an anonymous user.");
    }

    public void setFirstname(String firstname) {
        throw new AnonymousUserException("Cannot set firstname of an anonymous user.");
    }

    public void setFunction(String function) {
        throw new AnonymousUserException("Cannot set function of an anonymous user.");
    }

    public void setInitials(String initials) {
        throw new AnonymousUserException("Cannot set initials of an anonymous user.");
    }

    public void setOrganization(String organization) {
        throw new AnonymousUserException("Cannot set organisation of an anonymous user.");
    }

    public void setPostalCode(String postalCode) {
        throw new AnonymousUserException("Cannot set postal code of an anonymous user.");
    }

    public void setPrefixes(String prefixes) {
        throw new AnonymousUserException("Cannot set prefixes of an anonymous user.");
    }

    public void setSurname(String surname) {
        throw new AnonymousUserException("Cannot set surname of an anonymous user.");
    }

    public void setTelephone(String telephone) {
        throw new AnonymousUserException("Cannot set telephone of an anonymous user.");
    }

    public void setTitle(String title) {
        throw new AnonymousUserException("Cannot set title of an anonymous user.");
    }

    public void synchronizeOn(Person otherPerson) {
        throw new AnonymousUserException("Cannot synchronize on an anonymous user.");
    }

    public String getId() {
        throw new AnonymousUserException("Cannot get id of an anonymous user.");
    }

    public boolean isAnonymous() {
        return true;
    }

    @Override
    public String getAlternativeTelephone() {
        throw new AnonymousUserException("Method not supported.");
    }

    @Override
    public void setAlternativeTelephone(String altTel) {
        throw new AnonymousUserException("Method not supported.");
    }

    @Override
    public String toString() {
        return "anonymous user";
    }

    @Override
    public boolean isLogMyActions() {
        throw new AnonymousUserException("Method not supported.");
    }

    @Override
    public void setLogMyActions(boolean logMyActions) {
        throw new AnonymousUserException("Method not supported.");
    }

    @Override
    public String getDai() {
        throw new AnonymousUserException("Cannot get DAI of an anonymous user.");
    }

    @Override
    public void setDai(String dai) {
        throw new AnonymousUserException("Cannot set DAI of an anonymous user.");
    }

    @Override
    public void setDiscipline1(String discipline1) {
        throw new AnonymousUserException("Method not supported.");
    }

    @Override
    public String getDiscipline1() {
        throw new AnonymousUserException("Method not supported.");
    }

    @Override
    public void setDiscipline2(String discipline2) {
        throw new AnonymousUserException("Method not supported.");
    }

    @Override
    public String getDiscipline2() {
        throw new AnonymousUserException("Method not supported.");
    }

    @Override
    public void setDiscipline3(String discipline3) {
        throw new AnonymousUserException("Method not supported.");
    }

    @Override
    public String getDiscipline3() {
        throw new AnonymousUserException("Method not supported.");
    }

    @Override
    public boolean hasAcceptedGeneralConditions() {
        return false;
    }

    @Override
    public void setAcceptedGeneralConditions(boolean accepted) {
        throw new AnonymousUserException("Method not supported.");
    }

    @Override
    public boolean isSwordDepositAllowed() {
        return false;
    }

    @Override
    public void setSwordDepositAllowed(boolean swordDepositAllowed) {
        throw new AnonymousUserException("Method not supported.");
    }

}
