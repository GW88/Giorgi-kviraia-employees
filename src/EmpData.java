
import java.util.Date;
import java.util.Objects;

/**
 * container class that will store the project id and total days worked by the employee
 */
class EmpData {

    private long empId;
    private long projectID;
    private Date dateFrom;
    private Date dateTo;

    public EmpData(long empId, long projectID, Date dateFrom, Date dateTo) {
        this.empId = empId;
        this.projectID = projectID;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
    }

    public long getEmpId() {
        return empId;
    }

    public void setEmpId(long empId) {
        this.empId = empId;
    }

    public long getProjectID() {
        return projectID;
    }

    public void setProjectID(long projectID) {
        this.projectID = projectID;
    }

    public Date getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Date getDateTo() {
        return dateTo;
    }

    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmpData empData = (EmpData) o;
        return empId == empData.empId &&
                projectID == empData.projectID &&
                dateFrom.equals(empData.dateFrom) &&
                dateTo.equals(empData.dateTo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(empId, projectID, dateFrom, dateTo);
    }

    @Override
    public String toString() {
        return "EmpData{" +
                "empId=" + empId +
                ", projectID=" + projectID +
                ", dateFrom=" + dateFrom +
                ", dateTo=" + dateTo +
                '}';
    }

}
