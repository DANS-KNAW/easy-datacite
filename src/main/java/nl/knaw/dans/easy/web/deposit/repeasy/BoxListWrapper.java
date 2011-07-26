package nl.knaw.dans.easy.web.deposit.repeasy;

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.easy.domain.deposit.discipline.KeyValuePair;
import nl.knaw.dans.easy.domain.model.emd.types.Spatial;
import nl.knaw.dans.easy.domain.model.emd.types.Spatial.Box;
import nl.knaw.dans.easy.web.deposit.repeater.AbstractDefaultListWrapper;
import nl.knaw.dans.easy.web.deposit.repeater.AbstractEasyModel;
import nl.knaw.dans.easy.web.wicket.KvpChoiceRenderer;

import org.apache.wicket.markup.html.form.ChoiceRenderer;

public class BoxListWrapper extends AbstractDefaultListWrapper<BoxListWrapper.BoxModel, Spatial>
{

    private static final long serialVersionUID = -8745696945204069167L;

    public BoxListWrapper(List<Spatial> wrappedList)
    {
        super(wrappedList);
    }
    
    public List<BoxModel> getInitialItems()
    {
        List<BoxModel> listItems = new ArrayList<BoxModel>();
        for (Spatial spatial : getWrappedList())
        {
            if (spatial.getBox() != null)
            {
                listItems.add(new BoxModel(spatial));
            }
        }
        return listItems;
    }

    public int synchronize(List<BoxModel> listItems)
    {
        getWrappedList().removeAll(getFilteredList());
        
        // add new entries
        int errors = 0;
        for (int i = 0; i < listItems.size(); i++)
        {
            BoxModel model = listItems.get(i);
            Spatial spatial = model.getSpatial();
            if (spatial != null)
            {
                getWrappedList().add(spatial);
            }
            if (model.hasErrors())
            {
                handleErrors(model.getErrors(), i);
                errors += model.getErrors().size();
            }
            model.clearErrors();
        }
        return errors;
    }
    
    @Override
    public BoxModel getEmptyValue()
    {
    	BoxModel model = new BoxModel();
        return model;
    }
    
    @Override
    public ChoiceRenderer getChoiceRenderer()
    {
        return new KvpChoiceRenderer();
    }
    
    private List<Spatial> getFilteredList()
    {
        List<Spatial> filtered = new ArrayList<Spatial>();
        for (Spatial spatial : getWrappedList())
        {
            if (spatial.getBox() != null)
            {
                filtered.add(spatial);
            }
        }
        return filtered;
    }
    
    public static class BoxModel extends AbstractEasyModel
    {

        private static final long serialVersionUID = 3841830253279006843L;
        
        private String schemeToken;
        private String north;
        private String east;
        private String south;
        private String west;
        
        public BoxModel(Spatial spatial)
        {
            if (spatial.getBox() == null)
            {
                throw new IllegalArgumentException("Model for spatial box cannot be created.");
                // you don't get the point.
            }
            schemeToken = spatial.getBox().getScheme();
//            north = convertToString(spatial.getBox().getNorth());
//            east = convertToString(spatial.getBox().getEast());
//            south = convertToString(spatial.getBox().getSouth());
//            west = convertToString(spatial.getBox().getWest());
            north = spatial.getBox().getNorth();
            east = spatial.getBox().getEast();
            south = spatial.getBox().getSouth();
            west = spatial.getBox().getWest();
        }
        
        protected BoxModel()
        {           
        }
        
        public Spatial getSpatial()
        {
            Spatial spatial;
            if (schemeToken == null && north == null && east == null && south == null && west == null)
            {
                spatial = null;
            }
            else
            {
                spatial = new Spatial();
//                spatial.setBox(new Box(schemeToken, 
//                        convertToDouble(north, "North"), 
//                        convertToDouble(east, "East"),
//                        convertToDouble(south, "South"), 
//                        convertToDouble(west, "West")));
                spatial.setBox(new Box(schemeToken, north, east, south, west));
            }
            return spatial;
        }

        public void setScheme(KeyValuePair schemeKVP)
        {
            schemeToken = schemeKVP == null ? null : schemeKVP.getKey();
        }
        
        public KeyValuePair getScheme()
        {
            return new KeyValuePair(schemeToken, null);
        }
        
        public String getNorth() {
			return north;
		}

		public void setNorth(String north) {
			this.north = north;
		}

		public String getEast() {
			return east;
		}

		public void setEast(String east) {
			this.east = east;
		}

		public String getSouth() {
			return south;
		}

		public void setSouth(String south) {
			this.south = south;
		}

		public String getWest() {
			return west;
		}

		public void setWest(String west) {
			this.west = west;
		}
		
    }

}
