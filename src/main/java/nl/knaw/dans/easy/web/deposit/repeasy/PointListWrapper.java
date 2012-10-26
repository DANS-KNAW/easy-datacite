package nl.knaw.dans.easy.web.deposit.repeasy;

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.easy.domain.deposit.discipline.KeyValuePair;
import nl.knaw.dans.easy.domain.model.emd.types.Spatial;
import nl.knaw.dans.easy.domain.model.emd.types.Spatial.Point;
import nl.knaw.dans.easy.web.deposit.repeater.AbstractDefaultListWrapper;
import nl.knaw.dans.easy.web.deposit.repeater.AbstractEasyModel;
import nl.knaw.dans.easy.web.wicket.KvpChoiceRenderer;

import org.apache.wicket.markup.html.form.ChoiceRenderer;

public class PointListWrapper extends AbstractDefaultListWrapper<PointListWrapper.PointModel, Spatial>
{

    private static final long serialVersionUID = -8745696945204069167L;

    public PointListWrapper(List<Spatial> wrappedList)
    {
        super(wrappedList);
    }

    public List<PointModel> getInitialItems()
    {
        List<PointModel> listItems = new ArrayList<PointModel>();
        for (Spatial spatial : getWrappedList())
        {
            if (spatial.getPoint() != null)
            {
                listItems.add(new PointModel(spatial));
            }
        }
        return listItems;
    }

    public int synchronize(List<PointModel> listItems)
    {
        getWrappedList().removeAll(getFilteredList());
        // add new entries
        int errors = 0;
        for (int i = 0; i < listItems.size(); i++)
        {
            PointModel model = listItems.get(i);
            Spatial spatial = model.getSpatial();
            if (spatial != null)
            {
                getWrappedList().add(spatial);

                if (model.hasErrors())
                {
                    handleErrors(model.getErrors(), i);
                    errors += model.getErrors().size();
                }
                model.clearErrors();
            }
        }
        return errors;
    }

    @Override
    public PointModel getEmptyValue()
    {
        PointModel model = new PointModel();
        return model;
    }

    @Override
    public ChoiceRenderer<?> getChoiceRenderer()
    {
        return new KvpChoiceRenderer();
    }

    private List<Spatial> getFilteredList()
    {
        List<Spatial> filtered = new ArrayList<Spatial>();
        for (Spatial spatial : getWrappedList())
        {
            if (spatial.getPoint() != null)
            {
                filtered.add(spatial);
            }
        }
        return filtered;
    }

    public static class PointModel extends AbstractEasyModel
    {

        private static final long serialVersionUID = 3841830253279006843L;

        private String schemeToken;
        private String x;
        private String y;

        public PointModel(Spatial spatial)
        {
            if (spatial.getPoint() == null)
            {
                throw new IllegalArgumentException("Cannot model a spatial without a point! (Do you get the point?)");
            }
            schemeToken = spatial.getPoint().getScheme();
            //            x = convertToString(spatial.getPoint().getX());
            //            y = convertToString(spatial.getPoint().getY());
            x = spatial.getPoint().getX();
            y = spatial.getPoint().getY();
        }

        protected PointModel()
        {
        }

        public Spatial getSpatial()
        {
            Spatial spatial;
            if (schemeToken == null && x == null && y == null)
            {
                spatial = null;
            }
            else
            {
                spatial = new Spatial();
                //spatial.setPoint(new Point(schemeToken, convertToDouble(x, "X"), convertToDouble(y, "Y")));
                spatial.setPoint(new Point(schemeToken, x, y));
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

        public void setX(String x)
        {
            this.x = x;
        }

        public String getX()
        {
            return x;
        }

        public void setY(String y)
        {
            this.y = y;
        }

        public String getY()
        {
            return y;
        }

    }

}
