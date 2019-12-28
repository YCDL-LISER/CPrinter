package net.lingin.max.android.net.model;

import java.io.Serializable;

/**
 * @author Administrator
 */
public class LoginResponse implements Serializable {

    /**
     * 用户ID
     */
    private String operId;

    /**
     * 姓名
     */
    private String operName;

    /**
     * 分店编号
     */
    private String branchNo;

    /**
     * 分店名称
     */
    private String branchName;

    private String property;

    /**
     * 分店地址
     */
    private String address;

    public String getOperId() {
        return operId;
    }

    public void setOperId(String operId) {
        this.operId = operId;
    }

    public String getOperName() {
        return operName;
    }

    public void setOperName(String operName) {
        this.operName = operName;
    }

    public String getBranchNo() {
        return branchNo;
    }

    public void setBranchNo(String branchNo) {
        this.branchNo = branchNo;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
