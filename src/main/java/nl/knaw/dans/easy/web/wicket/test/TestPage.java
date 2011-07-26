package nl.knaw.dans.easy.web.wicket.test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.easy.domain.deposit.discipline.ChoiceList;
import nl.knaw.dans.easy.domain.deposit.discipline.KeyValuePair;
import nl.knaw.dans.easy.domain.model.emd.EasyMetadata;
import nl.knaw.dans.easy.domain.model.emd.EasyMetadataFactory;
import nl.knaw.dans.easy.domain.model.emd.types.Spatial;
import nl.knaw.dans.easy.domain.model.emd.types.ApplicationSpecific.MetadataFormat;
import nl.knaw.dans.easy.domain.workflow.Remark;
import nl.knaw.dans.easy.web.deposit.repeasy.BasicStringListWrapper;
import nl.knaw.dans.easy.web.deposit.repeasy.PointListWrapper;
import nl.knaw.dans.easy.web.deposit.repeater.AbstractRepeaterPanel;
import nl.knaw.dans.easy.web.deposit.repeater.PointPanel;
import nl.knaw.dans.easy.web.deposit.repeater.TextFieldPanel;
import nl.knaw.dans.easy.web.main.AbstractEasyNavPage;
import nl.knaw.dans.easy.web.template.AbstractEasyForm;
import nl.knaw.dans.easy.web.wicket.RemarksModel;
import nl.knaw.dans.easy.web.wicket.RemarksPanel;
import nl.knaw.dans.easy.web.wicket.SelectUserPanel;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestPage extends AbstractEasyNavPage
{
    private static final Logger logger = LoggerFactory.getLogger(TestPage.class);
    
    final EasyMetadata  emd = EasyMetadataFactory.newEasyMetadata(MetadataFormat.UNSPECIFIED);
    final EasyMetadata  emd2 = EasyMetadataFactory.newEasyMetadata(MetadataFormat.UNSPECIFIED);
    private final SelectUserPanel selectUserPanel;
    
    public TestPage()
    {
        super();
        selectUserPanel = new SelectUserPanel("selectUserPanel");
        
        init();
    }
    
    private void init()
    {
        Entity entity = new Entity();
        entity.setLanguage("eng");
        TestDepoModel depoModel = new TestDepoModel(entity);
        
        IModel model = new CompoundPropertyModel(depoModel);
        TestForm testForm = new TestForm("form", model);
        
        testForm.add(selectUserPanel);
        
        List<Remark> remarkList = new ArrayList<Remark>();
        Remark remark = new Remark("foo foo", "user");
        remarkList.add(remark);
        RemarksPanel remarksPanel = new RemarksPanel("remarksPanel", new RemarksModel(remarkList)
        {
            private static final long serialVersionUID = -3916972359698883016L;

            @Override
            public void onSubmit()
            {
                logger.debug("onSubmit of remarksPanel");
                
            }
            
        });
        testForm.add(remarksPanel);
        
        testForm.setAddExtra(true);
        add(testForm);
        
        DepoForm depoForm = new DepoForm("depoForm", null);
        PointListWrapper listWrapper2 = new PointListWrapper(emd2.getEmdCoverage().getEasSpatial());
        
        List<KeyValuePair> choices2 = new ArrayList<KeyValuePair>();
        choices2.add(new KeyValuePair("RD", "RD (in m.)"));
        choices2.add(new KeyValuePair("degrees", "lengte/breedte (graden)"));
        choices2.add(new KeyValuePair("locale", "lokale coordinaten"));
        ChoiceList choiceList = new ChoiceList(choices2);
        PointPanel pointPanel = new PointPanel("panel", listWrapper2, choiceList);
        pointPanel.setLabelResourceKey("label.spatialpoint");
        pointPanel.setInEditMode(true);
        depoForm.addPanel(pointPanel);
        
        BasicStringListWrapper bsListWrapper = new BasicStringListWrapper(emd2.getEmdTitle().getDcTitle());
        TextFieldPanel<String> titlePanel = new TextFieldPanel<String>("panel", bsListWrapper);
        depoForm.addPanel(titlePanel);
        
        
        
        
        add(depoForm);
        
        
    }
    
    class TestForm extends AbstractEasyForm
    {
        
        private boolean initiated;
        private boolean addExtra;
        
        

        private static final long serialVersionUID = -5278995552774414891L;

        public TestForm(String wicketId, IModel model)
        {
            super(wicketId, model);
            
        }
        
        public void setAddExtra(boolean addExtra)
        {
            this.addExtra = addExtra;
        }

        @Override
        protected void onSubmit()
        {
//            System.out.println("form on submit");
//            System.err.println("selected id=" + selectUserPanel.getSelectedId());
//            System.err.println("selected user=" + selectUserPanel.getSelectedUser());
            
         // synchronize sourceLists of business objects on listItems from RepeaterPanels.
            super.visitChildren(AbstractRepeaterPanel.class, new IVisitor()
            {

                public Object component(Component component)
                {
                    System.out.println("Visiting component " + component.getClass() + " >" + this.getClass());
                    ((AbstractRepeaterPanel<?>)component).synchronize();
                    return IVisitor.CONTINUE_TRAVERSAL;
                }
                
            });
            
            System.out.println("----SPATIAL---");
            for (Spatial spatial : emd.getEmdCoverage().getEasSpatial())
            {
                System.out.println("\t" + spatial);
            }
            
            TestDepoModel depoModel = (TestDepoModel) getDefaultModel().getObject();         
            Entity entity = depoModel.entity;
            System.out.println(entity);
            
            if (emd.getEmdCoverage().getEasSpatial().size() > 0)
            {
                if ("RD".equals(emd.getEmdCoverage().getEasSpatial().get(0).getPoint().getScheme()))
                {
                    PointPanel pointPanel = (PointPanel) get("spatialPoint");
                    pointPanel.info(0, "hello 0");
                    pointPanel.info(0, "another message");
                    pointPanel.error(1, "hello 1, you are totaly wrong!");
                    pointPanel.error(123456, "probably not an item within range.");
                }
            }
        }
        
        @Override
        protected void onBeforeRender()
        {
            //System.out.println("beforeRender");
            if (!initiated)
            {
                init();
                initiated = true;
            }
            super.onBeforeRender();
        }

        private void init()
        {
            System.out.println("init");
            final TestDepoModel depoModel = (TestDepoModel) getDefaultModel().getObject();
            
            List<TestChoiceItem> colorChoices = new ArrayList<TestChoiceItem>();
            colorChoices.add(new TestChoiceItem("wit", "white"));
            colorChoices.add(new TestChoiceItem("zwrt", "black"));
            colorChoices.add(new TestChoiceItem("grn", "green"));
            colorChoices.add(new TestChoiceItem("rd", "red"));
            if (addExtra)
            {
                colorChoices.add(new TestChoiceItem("bl", "blue"));
            }
            
            List<TestChoiceItem> languageChoices = new ArrayList<TestChoiceItem>();
            languageChoices.add(new TestChoiceItem("dut/nld", "Dutch"));
            languageChoices.add(new TestChoiceItem("eng", "English"));
            languageChoices.add(new TestChoiceItem("fr", "French"));
            
            final List<TestChoiceItem> typeChoices = new ArrayList<TestChoiceItem>();
            typeChoices.add(new TestChoiceItem("doc", "Word"));
            typeChoices.add(new TestChoiceItem("xls", "Excel"));
            typeChoices.add(new TestChoiceItem("mdb", "Access"));
            typeChoices.add(new TestChoiceItem("ppt", "PowerPoint"));
            
            final ChoiceRenderer renderer = new ChoiceRenderer("value", "key");
            
            DropDownChoice color = new DropDownChoice("color", new PropertyModel(depoModel, "color"), colorChoices, renderer);
            add(color);
            
            DropDownChoice language = new DropDownChoice("language", new PropertyModel(depoModel, "language"), languageChoices, renderer);
            add(language);
            
            
            ListView listView = new ListView("typelist", depoModel.getTypes())
            {
                private static final long serialVersionUID = 1L;

                @Override
                protected void populateItem(ListItem item)
                {
                    DropDownChoice type = new DropDownChoice("type", new PropertyModel(depoModel, "type." + item.getIndex()), typeChoices, renderer);
                    type.setNullValid(true);
                    item.add(type);
                }             
                
            };
            listView.setReuseItems(true);
            add(listView);
                        
            PointListWrapper listWrapper = new PointListWrapper(emd.getEmdCoverage().getEasSpatial());
            
            List<KeyValuePair> choices = new ArrayList<KeyValuePair>();
            choices.add(new KeyValuePair("RD", "RD (in m.)"));
            choices.add(new KeyValuePair("degrees", "lengte/breedte (graden)"));
            choices.add(new KeyValuePair("locale", "lokale coordinaten"));
            ChoiceList choiceList = new ChoiceList(choices);
            PointPanel spatialPointPanel = new PointPanel("spatialPoint", listWrapper, choiceList);
            spatialPointPanel.setInEditMode(true);
            spatialPointPanel.setLabelResourceKey("label.spatialpoint");
            add(spatialPointPanel);
            spatialPointPanel.info("hello feedback");
        }
        
//        @Override
//        protected void onAfterRender()
//        {
//            System.out.println("beforeRender");
//            super.onAfterRender();
//        }
        
//        @Override
//        protected void onComponentTag(ComponentTag tag)
//        {
//            System.out.println("onCoponentTag ");
//            super.onComponentTag(tag);
//        }
        
//        @Override
//        protected void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag)
//        {
//            System.out.println("onComponentTagBody ");
//            super.onComponentTagBody(markupStream, openTag);
//        }
        
//        @Override
//        protected void onDetach()
//        {
//            System.out.println("onDetach. We are leaving the form?");
//            System.out.println("getRequestCycle().getRequestTarget()=" + getRequestCycle().getRequestTarget());
//            IRequestTarget target = getRequestCycle().getRequestTarget();
//            if (target instanceof IPageRequestTarget)
//            {
//                if (!((IPageRequestTarget)target).getPage().equals(this.getPage()))
//                {
//                    System.out.println("We are leaving the page. Alert user to save or not.");
//                }
//            }
//            super.onDetach();
//        }
        
        @Override
        protected void onError()
        {
            System.out.println("onError");
            super.onError();
        }
        
        @Override
        protected void onModelChanged()
        {
            System.out.println("onModalChanged");
            super.onModelChanged();
        }
        
        @Override
        protected void onModelChanging()
        {
            System.out.println("onModelChanging");
            super.onModelChanging();
        }
        
//        @Override
//        protected void onRender(MarkupStream markupStream)
//        {
//            System.out.println("onRender ");
//            super.onRender(markupStream);
//        }

        
    }
    
    
    
    
    public class TestDepoModel implements Serializable
    {

        private static final long serialVersionUID = 1761970515478635544L;
        private final Entity entity;
        List<TestChoiceItem> types = new ArrayList<TestChoiceItem>();
        
        public TestDepoModel(Entity entity)
        {
            this.entity = entity;
            
        }
        
        public TestChoiceItem getColor()
        {
            return new TestChoiceItem(entity.getColor(), null);
        }

        public void setColor(TestChoiceItem color)
        {
            entity.setColor(color == null ? null : color.getKey());
        }

        public TestChoiceItem getLanguage()
        {
            return new TestChoiceItem(entity.getLanguage(), null);
        }

        public void setLanguage(TestChoiceItem language)
        {
            entity.setLanguage(language == null ? null : language.getKey());
        }
        
        public List<TestChoiceItem> getTypes()
        {  
            List<TestChoiceItem> types = new ArrayList<TestChoiceItem>();
            for (String type : entity.getTypes())
            {
                types.add(new TestChoiceItem(type, null));
            }
            if (types.isEmpty())
            {
                types.add(new TestChoiceItem(null, null));
            }
            return types;
        }
        
        public TestChoiceItem getType(int index)
        {
            if (index >= entity.getTypes().size())
            {
                return new TestChoiceItem(null, null);
            }
            else
            {
                return new TestChoiceItem(entity.getTypes().get(index), null);
            }
        }
        
        public void setType(int index, TestChoiceItem type)
        {
            System.out.println(index);
            if (index >= entity.getTypes().size())
            {
                if (type != null && type.getKey() != null)
                {
                    entity.addType(type.getKey()); 
                    System.out.println("add 1");
                }                               
            }
            else
            {
                entity.getTypes().remove(index);
                if (type != null && type.getKey() != null)
                {
                    entity.getTypes().add(index, type.getKey());
                    System.out.println("add 2");
                }
                    
                    
            }
        }
        
    }
    
    public class Entity implements Serializable
    {

        private static final long serialVersionUID = 9200979461412844294L;
        
        private String color;
        private String language;
        private List<String> types = new ArrayList<String>();
        
        public String getColor()
        {
            return color;
        }
        
        public void setColor(String color)
        {
            this.color = color;
        }
        
        public String getLanguage()
        {
            return language;
        }
        
        public void setLanguage(String language)
        {
            this.language = language;
        }
        
        public List<String> getTypes()
        {
            return types;
        }
        
        public void addType(String type)
        {
            types.add(type);
        }
        
        @Override
        public String toString()
        {
            StringBuilder sb = new StringBuilder();
            for (String type : getTypes())
            {
                sb.append(type + " | ");
            }
            return super.toString() + " color=" + color + " language=" + language + " types=" + sb.toString();
        }
        
    }
    
    public class TestChoiceItem implements Serializable
    {

        private static final long serialVersionUID = -8485710122828504906L;
        
        private String key;
        private String value;
        
        public TestChoiceItem()
        {
            
        }
        
        public TestChoiceItem(String key, String value)
        {
            this.key = key;
            this.value = value;
        }

        public String getKey()
        {
            return key;
        }

        public void setKey(String key)
        {
            this.key = key;
        }

        public String getValue()
        {
            return value;
        }

        public void setValue(String value)
        {
            this.value = value;
        }
        
        
        
    }
    
    
    
    

}
