package net.arville.easybill.payload;

public class GeneralOutput extends OutputStructure<Object> {
    public GeneralOutput(Object data) {
        super(data);
    }

    public GeneralOutput() {
    }

    @Override
    public Object getData() {
        return super.data;
    }

    @Override
    public void setData(Object data) {
        super.data = data;
    }
}
