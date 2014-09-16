/*******************************************************************************
 * Copyright (c) 2014 Benjamin Weißenfels.
 *
 * This program is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 *
 * Contributors:
 *     Benjamin Weißenfels <bw[at]sernet[dot]de> - initial API and implementation
 ******************************************************************************/
package sernet.verinice.rcp.accountgroup;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import sernet.gs.ui.rcp.main.service.ServiceFactory;
import sernet.verinice.interfaces.IAccountSearchParameter;
import sernet.verinice.interfaces.IAccountService;
import sernet.verinice.model.common.accountgroup.AccountGroup;
import sernet.verinice.model.common.configuration.Configuration;
import sernet.verinice.service.account.AccountSearchParameterFactory;

/**
 * @author Benjamin Weißenfels <bw[at]sernet[dot]de>
 *
 */
public class AccountGroupDataService implements IAccountGroupViewDataService {

    private Logger log = Logger.getLogger(AccountGroupDataService.class);

    private IAccountService accountService;

    private Map<String, Configuration> usernameToConfiguration;

    private Map<String, Set<String>> accountGroupToConfiguration;

    private Set<Configuration> accounts;

    public AccountGroupDataService() {
        accountService = ServiceFactory.lookupAccountService();
        loadAccountGroupData();
    }

    @Override
    public String[] getAccountGroups() {
        return convertToStringArray(accountGroupToConfiguration.keySet());
    }

    @Override
    public String[] getAllAccounts() {
        return convertToStringArray(accounts);
    }

    @Override
    final public void loadAccountGroupData() {

        List<AccountGroup> accountGroups = accountService.listGroups();
        accountGroupToConfiguration = new HashMap<String, Set<String>>();
        usernameToConfiguration = new HashMap<String, Configuration>();

        for (AccountGroup accountGroup : accountGroups) {
            IAccountSearchParameter parameter = AccountSearchParameterFactory.createAccountGroupParameter(accountGroup.getName());
            List<Configuration> configurationsForAccountGroup = accountService.findAccounts(parameter);
            accountGroupToConfiguration.put(accountGroup.getName(), new HashSet<String>());
            for (Configuration account : configurationsForAccountGroup)
                accountGroupToConfiguration.get(accountGroup.getName()).add(account.getUser());
        }

        accounts = new HashSet<Configuration>(accountService.listAccounts());

        for(Configuration account : accounts){
            usernameToConfiguration.put(account.getUser(), account);
        }
    }

    @Override
    public String[] getAccountNamesForGroup(String accountGroupName) {
        return convertToStringArray(accountGroupToConfiguration.get(accountGroupName));
    }

    private <T> String[] convertToStringArray(Set<T> accountGroupOrConfiguration) {

        Set<String> set = new HashSet<String>();
        for (T accountOrGroup : accountGroupOrConfiguration) {
            if (accountOrGroup instanceof AccountGroup) {
                set.add(((AccountGroup) accountOrGroup).getName());
            } else if (accountOrGroup instanceof Configuration) {
                set.add(((Configuration) accountOrGroup).getUser());
            } else if (accountOrGroup instanceof String) {
                set.add((String) accountOrGroup);
            } else {
                throw new IllegalArgumentException(String.format("%s is not supported", accountOrGroup.getClass().getSimpleName()));
            }
        }

        String[] result = new String[set.size()];
        set.toArray(result);
        return result;
    }

    @Override
    public void addAccountGroup(String accountGroupName) {
        AccountGroup accountGroup = new AccountGroup(accountGroupName);
        if (!accountGroupToConfiguration.containsKey(accountGroup)) {
            accountGroupToConfiguration.put(accountGroup.getName(), new HashSet<String>());
        }
    }

    @Override
    public String[] saveAccountGroupData(String groupName, String[] accountNames) {
        try {

            Set<Configuration> selectedAccounts = getSelectedConfigurations(accountNames);
            selectedAccounts = accountService.addRole(selectedAccounts, groupName);

            for (Configuration account  : selectedAccounts)
                accountGroupToConfiguration.get(groupName).add(account.getUser());

            return convertToStringArray(accountGroupToConfiguration.get(groupName));

        } catch (Exception ex) {
            log.error("updated view for account groups failed", ex);
        }

        return new String[] {};
    }

    @Override
    public void editAccountGroupName(String newName, String oldName) {

        // delete role from configurations

        accountGroupToConfiguration.put(newName, accountGroupToConfiguration.get(oldName));
        accountGroupToConfiguration.remove(oldName);

        deleteAccountGroup(oldName);
    }

    @Override
    public Set<Configuration> deleteAccountGroup(String groupName) {

        Set<String> accounts = accountGroupToConfiguration.get(groupName);
        accountGroupToConfiguration.remove(groupName);

        Set<Configuration> victims = new HashSet<Configuration>();
        for(String account : accounts)
        {
            victims.add(usernameToConfiguration.get(account));
        }

        return accountService.deleteRole(victims, groupName);
    }

    @Override
    public String[] deleteAccountGroupData(String groupName, String[] userNames) {

        accountGroupToConfiguration.get(groupName).removeAll(new HashSet<String>(Arrays.asList(userNames)));
        Set<Configuration> selectedAccounts = getSelectedConfigurations(userNames);

        Set<Configuration> deletedAccounts = accountService.deleteRole(selectedAccounts, groupName);
        return convertToStringArray(deletedAccounts);
    }

    private Set<Configuration> getSelectedConfigurations(String[] userNames) {

        Set<Configuration> selectedAccounts = new HashSet<Configuration>();
        for(String userName : userNames)
        {
            selectedAccounts.add(usernameToConfiguration.get(userName));
        }

        return selectedAccounts;
    }

}
