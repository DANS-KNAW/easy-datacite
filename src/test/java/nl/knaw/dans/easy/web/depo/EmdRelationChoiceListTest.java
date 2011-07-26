/**
 * 
 */
package nl.knaw.dans.easy.web.depo;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.business.services.EasyDepositService;
import nl.knaw.dans.easy.domain.deposit.discipline.ChoiceList;
import nl.knaw.dans.easy.domain.deposit.discipline.KeyValuePair;
import nl.knaw.dans.easy.domain.form.ChoiceListDefinition;
import nl.knaw.dans.easy.domain.model.emd.EmdRelation;
import nl.knaw.dans.easy.servicelayer.services.DepositService;
import nl.knaw.dans.easy.web.template.AbstractTestPage;

import org.junit.Test;

// TODO: Wat heeft deze test met de webui te maken?
/**
 * @author akmi
 *
 */
public class EmdRelationChoiceListTest extends AbstractTestPage
{
	@Test
	public void testRelationChoiceList() throws ServiceException
	{
		DepositService depositService = new EasyDepositService();
		ChoiceListDefinition clDef = new ChoiceListDefinition("common.dcterms.relation");
		ChoiceList choiceList = depositService.getChoices(clDef.getId(), null);
		List<KeyValuePair> kvpList = choiceList.getChoices();
		List<String> emdListKeys = Arrays.asList(EmdRelation.LIST_KEYS);
		//check the choicelist size.
		assertTrue("The size of choice list must not be longer than " + emdListKeys.size(), kvpList.size() <= emdListKeys.size());
		
		for (KeyValuePair kvp : kvpList)
		{
			//check weather the given choice list key exist in the emd relation list keys.
			assertTrue("This keys is not found:" + kvp.getKey(),emdListKeys.contains(kvp.getKey()));
		}
	}
}
