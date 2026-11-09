BARANGAY RECORDS MANAGEMENT SYSTEM  (3 JFrames + MySQL)

WINDOWS (your NetBeans JFrame forms, with your navigation buttons)
- ResidentView -> residents table            (ResidentController)
- Certificate  -> certificate_requests table (CertificateController)
- Blotter      -> blotter_records table      (BlotterController)
The app starts on ResidentView (BarangayRecordsManagementSystem.java).

SETUP
1. Run database.sql in MySQL Workbench (creates schema barangay_records + 3 tables).
2. Open DB.java and set DB_USER / DB_PASSWORD to your MySQL login.
3. Clean and Build in NetBeans (Maven downloads MySQL Connector/J), then Run.

DATABASE CODE
DB.java (connection + error messages), DAO.java (residents), CertificateDAO.java,
BlotterDAO.java, plus model classes Resident / CertificateRequest / BlotterRecord.
Certificate requests and blotters are linked to a resident; deleting a resident
also deletes their requests and blotters.
The Certificate form has no Status field, so certificate_requests.status keeps its
database default ('Pending').

Hand-written code in each JFrame is in customizeForm() at the bottom (outside the
generated block). Component names were renamed (txtFirstName, btnAdd, tblResidents...)
because the controllers use them; jButton7/jButton8 (navigation) were left as you made them.
