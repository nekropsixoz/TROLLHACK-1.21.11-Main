/**
 * Press F
 *           dll
 * @BySekyndaNeverbaev
 * @ForXKSGuard
 * */
package xks.accounts;
public class DataSubReader {
    boolean XKS__BOOST = true;
    public DataSubReader() {

    }
    public void setTrue(){
        XKS__BOOST = true;
    }
    public void setFalse(){
        XKS__BOOST = false;
    }

    public void checkBoost(){
        if(!XKS__BOOST){
            System.exit(52);
        }
    }
}