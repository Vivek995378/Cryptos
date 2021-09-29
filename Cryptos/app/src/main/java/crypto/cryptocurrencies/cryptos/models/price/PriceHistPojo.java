package crypto.cryptocurrencies.cryptos.models.price;

public class PriceHistPojo {

    private String Response;

    private String Type;

    private String Message;

    private String HasWarning;

    private PriceHistDataList Data;

    public String getResponse ()
    {
        return Response;
    }

    public void setResponse (String Response)
    {
        this.Response = Response;
    }

    public String getType ()
    {
        return Type;
    }

    public void setType (String Type)
    {
        this.Type = Type;
    }

    public String getMessage ()
    {
        return Message;
    }

    public void setMessage (String Message)
    {
        this.Message = Message;
    }

    public String getHasWarning ()
    {
        return HasWarning;
    }

    public void setHasWarning (String HasWarning)
    {
        this.HasWarning = HasWarning;
    }

    public PriceHistDataList getData ()
    {
        return Data;
    }

    public void setData (PriceHistDataList Data)
    {
        this.Data = Data;
    }

}
