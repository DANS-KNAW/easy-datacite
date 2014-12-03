package nl.knaw.dans.easy.web.wicket;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.knaw.dans.easy.domain.form.FormPage;
import nl.knaw.dans.easy.domain.form.PanelDefinition;
import nl.knaw.dans.easy.domain.form.StandardPanelDefinition;
import nl.knaw.dans.easy.domain.form.SubHeadingDefinition;
import nl.knaw.dans.easy.web.deposit.repeater.AbstractRepeaterPanel;
import nl.knaw.dans.easy.web.deposit.repeater.SkeletonPanel;

import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;

public class RecursivePanel extends Panel {

    public static final String DEFAULT_CSS_CONTAINER = "recursiveContainerL";

    public static final String DEFAULT_CSS_HEADING = "recursiveHeadingL";

    public static final String PANEL_WICKET_ID = "recursivePanel";

    private static final long serialVersionUID = 775750798125197686L;

    // private static final Logger logger = LoggerFactory.getLogger(RecursivePanel.class);

    private final IPanelFactory panelFactory;
    private String headResourceKey;
    private List<PanelDefinition> panelDefinitions;
    private String cssContainerClassName;
    private String cssHeadingClassName;
    private int level;
    private boolean initiated;

    private boolean editable;
    private boolean headVisible = true;

    // Keep panels in a map:
    // 1. (very important) plus-/minus buttons on the clients browser page aren't in the tree with new
    // panels;
    // 2. Panels remember their errors and new Panels have no knowledge of that;
    // 3. It saves a lot of instantiating.
    private Map<String, Panel> panelMap = new HashMap<String, Panel>();

    public RecursivePanel(final String id, final IPanelFactory panelFactory, final FormPage formPage) {
        this(id, panelFactory);
        headResourceKey = formPage.getLabelResourceKey();
        panelDefinitions = formPage.getPanelDefinitions();
        editable = formPage.isEditable();
        cssContainerClassName = formPage.getCssContainerClassName();
    }

    private RecursivePanel(final String id, final IPanelFactory panelFactory, final SubHeadingDefinition subPanelDefinition, final int level,
            final Map<String, Panel> panelMap, boolean editable)
    {
        this(id, panelFactory);
        this.level = level;
        this.panelMap = panelMap;
        headResourceKey = subPanelDefinition.getLabelResourceKey();
        panelDefinitions = subPanelDefinition.getPanelDefinitions();
        this.editable = editable;
    }

    private RecursivePanel(final String id, final IPanelFactory panelFactory) {
        super(id);
        setOutputMarkupId(true);
        this.panelFactory = panelFactory;
    }

    @Override
    protected void onBeforeRender() {
        if (!initiated) {
            init();
            initiated = true;
        }
        super.onBeforeRender();
    }

    private void init() {
        // logger.debug("Init of RecursivePanel");
        final WebMarkupContainer levelContainer = new WebMarkupContainer("levelContainer");
        levelContainer.setRenderBodyOnly(true);
        final Label head = new Label("head", new ResourceModel(headResourceKey, ""));
        head.add(new SimpleAttributeModifier("class", "h" + (level + 1)));
        head.setVisible(headVisible);
        levelContainer.add(head);

        @SuppressWarnings({"rawtypes", "unchecked"})
        final ListView listView = new ListView("recursivePanels", panelDefinitions) {

            private static final long serialVersionUID = 4962786970363087231L;

            @Override
            protected void populateItem(final ListItem item) {
                final PanelDefinition panelDefinition = (PanelDefinition) item.getDefaultModelObject();
                // /
                // System.err.println(panelDefinition.getId() + " " +
                // panelDefinition.getParent().getId());
                //
                if (panelDefinition instanceof SubHeadingDefinition) {
                    final SubHeadingDefinition spDef = (SubHeadingDefinition) panelDefinition;
                    item.add(new RecursivePanel(PANEL_WICKET_ID, panelFactory, spDef, level + 1, panelMap, editable).setRenderBodyOnly(true));
                } else if (panelDefinition instanceof StandardPanelDefinition) {

                    Panel panel = getPanel(panelDefinition);
                    final String label = getString(panelDefinition.getLabelResourceKey());

                    if (panel instanceof SkeletonPanel) {
                        SkeletonPanel skeletonPanel = (SkeletonPanel) panel;
                        skeletonPanel.setInEditMode(editable);

                        if (skeletonPanel.takesErrorMessages()) {
                            for (String msgKey : panelDefinition.getErrorMessages()) {
                                String msg = getString(msgKey, new Model(new LabelGetter(label)));
                                skeletonPanel.error(msg);
                            }
                        }
                    }

                    if (panel instanceof AbstractRepeaterPanel) {
                        AbstractRepeaterPanel<?> repeaterPanel = (AbstractRepeaterPanel<?>) panel;

                        if (repeaterPanel.takesErrorMessages()) {
                            for (Map.Entry<Integer, List<String>> entry : panelDefinition.getItemErrorMessages().entrySet()) {
                                int index = entry.getKey();
                                for (String msgKey : entry.getValue()) {
                                    String msg = getString(msgKey, new Model(new LabelGetter(label)));
                                    repeaterPanel.error(index, msg);
                                }
                            }
                        }
                    }

                    item.setOutputMarkupId(true);
                    item.add(panel);

                } else {
                    throw new PanelFactoryException("Unknown panel definition: " + panelDefinition.getClass());
                }
            }

        };
        listView.setRenderBodyOnly(true);
        levelContainer.add(listView);
        add(levelContainer);
    }

    private Panel getPanel(PanelDefinition panelDefinition) {
        Panel panel = panelMap.get(panelDefinition.getId());
        if (panel == null) {
            panel = panelFactory.createPanel((StandardPanelDefinition) panelDefinition);
            panelMap.put(panelDefinition.getId(), panel);
        }
        return panel;
    }

    /**
     * @return the cssContainerClassName
     */
    public String getCssContainerClassName() {
        if (cssContainerClassName == null) {
            cssContainerClassName = DEFAULT_CSS_CONTAINER;
        }
        return cssContainerClassName;
    }

    /**
     * @param cssContainerClassName
     *        the cssContainerClassName to set
     */
    public void setCssContainerClassName(String cssContainerClassName) {
        this.cssContainerClassName = cssContainerClassName;
    }

    /**
     * @return the cssHeadingClassName
     */
    public String getCssHeadingClassName() {
        if (cssHeadingClassName == null) {
            cssHeadingClassName = DEFAULT_CSS_HEADING;
        }
        return cssHeadingClassName;
    }

    /**
     * @param cssHeadingClassName
     *        the cssHeadingClassName to set
     */
    public void setCssHeadingClassName(String cssHeadingClassName) {
        this.cssHeadingClassName = cssHeadingClassName;
    }

    public boolean isHeadVisible() {
        return headVisible;
    }

    public void setHeadVisible(boolean headVisible) {
        this.headVisible = headVisible;
    }

    private class LabelGetter implements Serializable {

        private static final long serialVersionUID = 7606106662188748296L;
        private final String label;

        public LabelGetter(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

}
