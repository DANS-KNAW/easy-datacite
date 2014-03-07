package nl.knaw.dans.easy.domain.dataset.item;

public class ItemOrder
{

    private ItemOrderField field;

    private boolean ascending;

    public ItemOrder()
    {
    }

    public ItemOrder(ItemOrderField field, boolean ascending)
    {
        this.setField(field);
        this.setAscending(ascending);
    }

    public void setField(ItemOrderField field)
    {
        this.field = field;
    }

    public ItemOrderField getField()
    {
        return field;
    }

    public void setAscending(boolean ascending)
    {
        this.ascending = ascending;
    }

    public boolean isAscending()
    {
        return ascending;
    }
}
