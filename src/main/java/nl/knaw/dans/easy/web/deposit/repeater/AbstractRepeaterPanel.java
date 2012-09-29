	package nl.knaw.dans.easy.web.deposit.repeater;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nl.knaw.dans.easy.domain.form.StandardPanelDefinition;
import nl.knaw.dans.easy.web.wicket.EasyComponentFeedbackPanel;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A RepeaterPanel can show an extendible and shrinkable list of (form) components. The extendible list is shown in a
 * {@link ListView}. Subclasses of this AbstractRepeaterPanel are asked to contribute their repeating components on a
 * panel with {@link #getRepeatingComponentPanel(ListItem)}.
 * <p/>
 * The generic AbstractRepeaterPanel is parameterized with T extends Object. Let <code>item</code> be an item from this
 * RepeaterPanel ListView. Then, in pseudo code,
 * 
 * <pre>
 *    item.getDefaultModelObject() instance of T
 * </pre>
 * 
 * holds true.
 * <p/>
 * A RepeaterPanel shows at least one item, even when the list it represents is empty. Therefore the internal list
 * cannot be the same as the list it represents (<code>1 != 0</code>). The helper class {@link ListWrapper} converts and
 * synchronizes the two lists.
 * <p/>
 * A FeedbackPanel with a ComponentFeedbackMessageFilter filtering messages from this RepeaterPanel is provided.
 * <p/>
 * Each item in the ListView has a FeedbackPanel. The itemFeedback can be reached with {@link #error(int, String)} and
 * {@link #info(int, String)}, where the int-parameter corresponds to the item index.
 * 
 * @author ecco Apr 2, 2009
 * @param <T>
 *        the type encapsulated by the ListView ListItem.
 */
public abstract class AbstractRepeaterPanel<T extends Object> extends SkeletonPanel
{

    /**
     * The wicketId for panels that contribute to ListItems.
     * 
     * @see #getRepeatingComponentPanel(ListItem)
     */
    public static final String        REPEATING_PANEL_ID = "repeatingPanel";

    private static final long         serialVersionUID   = -4194940323146017009L;
    private static Logger             logger             = LoggerFactory.getLogger(AbstractRepeaterPanel.class);

    private final ListWrapper<T>      listWrapper;
    private List<T>             listItems;
    private boolean                   repeating          = true;
    
	private Map<Integer, Set<String>> itemErrorMap;
    private Map<Integer, Set<String>> itemInfoMap;

    /**
     * Construct a new RepeaterPanel. The given <code>model</code> has a ListWrapper&lt;T> as object.
     * 
     * @param wicketId
     *        id of this panel
     * @param model
     *        a model of sort IModel&lt;ListWrapper&lt;T>>
     * @throws ClassCastException
     *         if the object of the given model is not ListWrapper&lt;T>
     */
    @SuppressWarnings("unchecked")
    public AbstractRepeaterPanel(final String wicketId, final IModel model) throws ClassCastException
    {
        this(wicketId, model, (ListWrapper<T>) model.getObject());
    }

    /**
     * Construct a new RepeaterPanel. Since there was nothing else to put there, the model of this panel has the given
     * ListWrapper as object.
     * 
     * @param wicketId
     *        id of this panel
     * @param listWrapper
     *        ListWrapper encapsulating the list to be represented
     * @see #AbstractRepeaterPanel(String, IModel, ListWrapper)
     */
    public AbstractRepeaterPanel(final String wicketId, final ListWrapper<T> listWrapper)
    {
        this(wicketId, new Model(listWrapper), listWrapper);
    }

    /**
     * Construct a new RepeaterPanel. The given Model is not used by this AbstractRepeaterPanel.
     * <p/>
     * Gets the initial list from the ListWrapper and, if the list is empty adds an empty value obtained from the
     * ListWrapper.
     * 
     * @param wicketId
     *        id of this panel
     * @param model
     *        not used, though obtainable with {@link #getDefaultModel()}
     * @param listWrapper
     *        ListWrapper encapsulating the list to be represented
     */
    public AbstractRepeaterPanel(final String wicketId, final IModel model, final ListWrapper<T> listWrapper)
    {
        super(wicketId, model);
        setOutputMarkupId(true);
        this.listWrapper = listWrapper;
        this.listWrapper.setComponent(this);
        //listItems = this.listWrapper.getInitialItems();
        
    }

    /**
     * {@inheritDoc}
     */
    public void setDefinition(final StandardPanelDefinition definition)
    {
        super.setDefinition(definition);
        this.repeating = definition.isRepeating();
    }

    /**
     * Get this RepeaterPanel's ListWrapper.
     * 
     * @return the listWrapper
     */
    public ListWrapper<T> getListWrapper()
    {
        return listWrapper;
    }

    /**
     * Get the internal list.
     * 
     * @return the internal list
     */
    public List<T> getListItems()
    {
        if (listItems == null)
        {
            if (isInEditMode())
            {
                listItems = listWrapper.getInitialEditableItems();
            }
            else
            {
                listItems = listWrapper.getInitialItems();
            }
        }
        return listItems;
    }

    /**
     * Synchronize the list in the listWrapper on this RepeaterPanel's internal list.
     * 
     * @return the amount of errors while synchronizing
     */
    public int synchronize()
    {
        if(isInEditMode())
        {
            return listWrapper.synchronize(listItems);
        }
        else
        {
            return 0;
        }
    }

    /**
     * Is this RepeaterPanel showing plus and minus buttons or not.
     * 
     * @return <code>true</code> if plus and minus buttons are visible, <code>false</code> otherwise
     */
    public boolean isRepeating()
    {
        return repeating;
    }
	
    /**
     * Should this RepeaterPanel show plus and minus buttons. The default is <code>true</code>: show plus and minus
     * buttons.
     * 
     * @param repeating
     *        <code>true</code> if plus and minus buttons should be visible, <code>false</code> otherwise
     * @throws IllegalStateException
     *         if called after rendering of this panel
     */
    public void setRepeating(boolean repeating) throws IllegalStateException
    {
        if (isInitiated())
        {
            throw new IllegalStateException("Cannot set representation state after rendering.");
        }
        this.repeating = repeating;
    }

    /**
     * Provide an info message for the item on <code>index</code>.
     * 
     * @param index
     *        index of the target item
     * @param message
     *        message to show
     */
    public void info(int index, String message)
    {
        if (itemInfoMap == null)
        {
            itemInfoMap = new HashMap<Integer, Set<String>>();
        }
        putMessage(itemInfoMap, index, message);
    }

    /**
     * Provide an error message for the item on <code>index</code>.
     * 
     * @param index
     *        index of the target item
     * @param message
     *        message to show
     */
    public void error(int index, String message)
    {
        if (itemErrorMap == null)
        {
            itemErrorMap = new HashMap<Integer, Set<String>>();
        }
        putMessage(itemErrorMap, index, message);
        logger.debug("Recorded error: index=" + index + " message=" + message);
    }

    /**
     * Handle the action for a plus-button click. Standard behavior of this method is to add an empty value to the
     * listItems of the ListView on this panel. An empty value is obtained by calling
     * {@link ListWrapper#getEmptyValue()} on this panels ListWrapper. Subclasses may override this method to perform
     * more complex actions.
     * 
     * @param target
     *        target that produces ajax response envelopes
     * @param form
     *        the form this panel is on
     */
    protected void handlePlusButtonClicked(AjaxRequestTarget target, Form form)
    {
        getListItems().add(getListWrapper().getEmptyValue());
    }

    /**
     * Handle the action for a minus-button click. Standard behavior of this method is to remove the item from the list
     * of Items of the ListView on this panel. and call synchronize on the wrapped list (see
     * {@link ListWrapper#synchronize(List)}). Subclasses may override this method to perform more complex actions.
     * 
     * @param item
     *        the item receiving the minus click
     * @param target
     *        target that produces ajax response envelopes
     * @param form
     *        the form this panel is on
     */
    protected void handleMinusButtonClicked(ListItem item, AjaxRequestTarget target, Form form)
    {
        getListItems().remove(item.getIndex());
        correctMessageMaps(item.getIndex());
    }

    /**
     * Contribute the repeating component(s) on a panel.
     * 
     * @param item
     *        item from the ListView on this panel
     * @return a panel with wicketId {@link #REPEATING_PANEL_ID}
     */
    protected abstract Panel getRepeatingComponentPanel(final ListItem<T> item);
    
    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();
        
        // actions required after a user hits refresh
        if (!isInEditMode() && listItems.isEmpty())
            this.getParent().setVisible(false); //if no data, don't display.
    }
    
    @SuppressWarnings("rawtypes")
    protected void init()
    {
        getListItems(); // make sure variable <listItems> is initialized 
        
        if (isInEditMode())
        {
            if (listItems.isEmpty())
            {
                listItems.add(this.listWrapper.getEmptyValue());
            }
        }
        else
    	{
    		super.setHelpItem(null);      //don't display help sign.
    		super.setRequired(false);     //don't display * sign.
    	}
        super.init(); // skeletonPanel

        final WebMarkupContainer listViewContainer = new WebMarkupContainer("listViewContainer")
        {
            private static final long serialVersionUID = -3643269307322617461L;

            @Override
            protected void onAfterRender()
            {
                //logger.debug("after render of listViewContainer");
                clearItemMessages();
                super.onAfterRender();
            }
        };

        @SuppressWarnings("unchecked")
        final ListView listView = new ListView("listView", listItems) 
        {

			private static final long serialVersionUID = -1211775601610374234L;

			
            @Override
			protected void populateItem(final ListItem item) 
			{
				item.add(new EasyComponentFeedbackPanel("itemFeedback", item));
				addItemMessages(item);

				// call on subclasses to contribute their repeating component(s)
				// on a panel.
				Panel repeatingComponentPanel = getRepeatingComponentPanel(item);
				item.add(repeatingComponentPanel);
				
				//create a holder of buttons minus and plus
				//display it when in edit mode.
				final WebMarkupContainer buttonsHolder = new WebMarkupContainer("buttonsHolder");
				if (isInEditMode()) 
				{
					// create and add the minus link.
					AjaxSubmitLink minusLink = new AjaxSubmitLink("minusLink") 
					{

						private static final long serialVersionUID = -1068301614895419955L;

						@Override
						protected void onSubmit(final AjaxRequestTarget target,
								final Form form) {
							logger
									.debug("onSubmit minusLink. removedItemIndex="
											+ item.getIndex());
							handleMinusButtonClicked(item, target, form);
							target.addComponent(listViewContainer);
						}

					};
					minusLink.setVisible(isRepeating() && listItems.size() != 1);
					buttonsHolder.add(minusLink);

					// create and add the plus link.
					AjaxSubmitLink plusLink = new AjaxSubmitLink("plusLink") 
					{

						private static final long serialVersionUID = -1068301614895419955L;

						@Override
						protected void onSubmit(final AjaxRequestTarget target,
								final Form form) {
							logger.debug("onSubmit plusLink");
							handlePlusButtonClicked(target, form);
							logger.debug("addComponent");
							target.addComponent(listViewContainer);
						}

					};
					plusLink.setVisible(isRepeating() && item.getIndex() == listItems.size() - 1);
					buttonsHolder.add(plusLink);
				} 
				else 
				{
					//hidden when in non editable mode.
					buttonsHolder.setVisible(false);
				}

				item.add(buttonsHolder);
			}

		};
        listViewContainer.add(listView);
        add(listViewContainer.setOutputMarkupId(true));

    }

    private void clearItemMessages()
    {
        if (itemInfoMap != null)
        {
            itemInfoMap.clear();
        }
        if (itemErrorMap != null)
        {
            itemErrorMap.clear();
        }
    }

    private void addItemMessages(ListItem item)
    {
        int infoos = 0;
        int errors = 0;

        if (itemInfoMap != null)
        {
            Set<String> infoMessages = itemInfoMap.get(item.getIndex());
            if (infoMessages != null)
            {
                for (String message : infoMessages)
                {
                    item.info(message);
                    infoos++;
                }
            }
        }

        if (itemErrorMap != null)
        {
            Set<String> errorMessages = itemErrorMap.get(item.getIndex());
            if (errorMessages != null)
            {
                for (String message : errorMessages)
                {
                    item.error(message);
                    errors++;
                }
            }
        }

        if (infoos > 0 || errors > 0)
        {
            logger
                    .debug("Added item messages on index " + item.getIndex() + ". infoos=" + infoos + " errors="
                            + errors);
        }
    }

    private void putMessage(Map<Integer, Set<String>> map, int index, String message)
    {
        Set<String> set = map.get(index);
        if (set == null)
        {
            // TODO refactor set back to list. double-error-recording-bug captured.
            set = new LinkedHashSet<String>();
            map.put(index, set);
        }
        set.add(message);
    }

    private void correctMessageMaps(int removedIndex)
    {
        if (itemErrorMap != null)
        {
            itemErrorMap.remove(removedIndex);
            Map<Integer, Set<String>> temp = new HashMap<Integer, Set<String>>();
            for (Integer integer : itemErrorMap.keySet())
            {
                int i = integer.intValue();
                if (i < removedIndex)
                {
                    temp.put(integer, itemErrorMap.get(integer));
                }
                else
                {
                    temp.put(i - 1, itemErrorMap.get(integer));
                }
            }
            itemErrorMap = temp;
        }

        if (itemInfoMap != null)
        {
            itemInfoMap.remove(removedIndex);
            Map<Integer, Set<String>> temp = new HashMap<Integer, Set<String>>();
            for (Integer integer : itemInfoMap.keySet())
            {
                int i = integer.intValue();
                if (i < removedIndex)
                {
                    temp.put(integer, itemInfoMap.get(integer));
                }
                else
                {
                    temp.put(i - 1, itemInfoMap.get(integer));
                }
            }
            itemInfoMap = temp;
        }

    }
}
