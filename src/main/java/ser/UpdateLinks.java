//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package ser;

import com.ser.blueline.*;
import com.ser.blueline.metaDataComponents.*;
import com.ser.foldermanager.*;
import de.ser.doxis4.agentserver.UnifiedAgent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class UpdateLinks extends UnifiedAgent {
    Logger log = LogManager.getLogger(this.getClass().getName());
    String nameDescriptor1 = "ccmPrjDocFileName";
    String nameDescriptorRev = "ccmPrjDocRevision";
    String searchClassName = "Search Engineering Documents";
    ISession ses = null;
    IDocumentServer srv = null;
    String prjCode = "";
    IDescriptor descriptor1;
    IDescriptor descriptor2;

    public UpdateLinks() {
    }

    protected Object execute() {
        this.log.info("Initiate the agent");
        if (this.getEventDocument() == null) {
            return this.resultError("Null Document object.");
        } else {
            ses = getSes();
            srv = ses.getDocumentServer();
            IDocument ldoc = this.getEventDocument();
            prjCode = ldoc.getDescriptorValue("ccmPRJCard_code");
            try {
                this.setParent(ldoc);
                this.updateTransmittals(ldoc);
                this.log.info("UpdateLinks Finished");
                return this.resultSuccess("Ended successfully");
            } catch (Exception var15) {
                throw new RuntimeException(var15);
            }
        }
    }
    public void updateTransmittals(IDocument engDocument) throws Exception {
        this.log.info("Start update transmittals.....");
        try {
            String trasnmittals = engDocument.getDescriptorValue("ccmPrjDocTransmittals");
            String crrsInc = engDocument.getDescriptorValue("ccmPrjDocTransIncCode");
            String crrsOut = engDocument.getDescriptorValue("ccmPrjDocTransOutCode");
            List<String> allTransmittals = getAllTransmittals(trasnmittals, crrsInc, crrsOut);

            engDocument.setDescriptorValue("ccmPrjDocTransmittals",String.join("\n",allTransmittals));
            engDocument.commit();
        }catch (Exception e){
            throw new Exception("Exeption Caught..updateTransmittals: " + e);
        }
    }

    private static List<String> getAllTransmittals(String trasnmittals, String crrsInc, String crrsOut) {
        List<String> currentTransmittals = new ArrayList<>();

        if(trasnmittals != null){
            currentTransmittals = new ArrayList<>(Arrays.asList(trasnmittals.split("\n")));
        }
        List<String> allTransmittals = new ArrayList<>(currentTransmittals);

        if(crrsInc != null && !allTransmittals.contains(crrsInc)){
            allTransmittals.add(crrsInc);
        }
        if(crrsOut != null && !allTransmittals.contains(crrsOut)){
            allTransmittals.add(crrsOut);
        }
        return allTransmittals;
    }

    public void setParent(IDocument engDocument) throws Exception {
        this.log.info("Start Link.....");
        try {
            String chkKeyPrnt = engDocument.getDescriptorValue("ccmPrjDocParentDoc");
            String chkKeyCrrsInc = engDocument.getDescriptorValue("ccmPrjDocTransIncCode");
            String chkKeyCrrsOut = engDocument.getDescriptorValue("ccmPrjDocTransOutCode");
            List<String> linkList = new ArrayList<>();

            this.log.info("Start Link for Parent Number:" + chkKeyPrnt + " child number:" + engDocument.getDescriptorValue("ccmPrjDocNumber"));
            if (prjCode != null){
                if(chkKeyPrnt != null){
                    IDocument prntDocument = getEngDocumentByNumber(ses, prjCode, chkKeyPrnt);
                    this.log.info("Parent Doc ? " + prntDocument);
                    if(prntDocument != null){
                        ILink[] links = srv.getReferencedRelationships(ses, prntDocument, true, false);
                        for(ILink link : links){
                            linkList.add(link.getTargetDocumentId());
                        }
                    }
                    if (prntDocument != null && !prntDocument.getDescriptorValue("ccmPrjDocCategory").trim().equalsIgnoreCase("TRANSMITTAL")) {
                        if (!Objects.equals(prntDocument.getID(), engDocument.getID()) && !linkList.contains(engDocument.getID())) {
                            ILink lnk2 = srv.createLink(ses, prntDocument.getID(), (INodeGeneric) null, engDocument.getID());
                            lnk2.commit();
                            engDocument.commit();
                            this.log.info("Created Link...");
                        }
                    }
                }
                if(chkKeyCrrsInc != null){
                    IDocument prntDocument = this.getEngDocumentByNumber(ses, prjCode, chkKeyCrrsInc);
                    this.log.info("Parent (CRRS) Doc ? " + prntDocument);
                    if(prntDocument != null){
                        ILink[] links = srv.getReferencedRelationships(ses, prntDocument, true, false);
                        for(ILink link : links){
                            linkList.add(link.getTargetDocumentId());
                        }
                    }
                    if (prntDocument != null && prntDocument.getDescriptorValue("ccmPrjDocCategory").trim().equalsIgnoreCase("TRANSMITTAL")) {
                        if (!Objects.equals(prntDocument.getID(), engDocument.getID()) && !linkList.contains(engDocument.getID())) {
                            ILink lnk2 = srv.createLink(ses, prntDocument.getID(), (INodeGeneric) null, engDocument.getID());
                            lnk2.commit();
                            engDocument.commit();
                            this.log.info("Created Link...");
                        }
                    }
                }
                if(chkKeyCrrsOut != null){
                    IDocument prntDocument = this.getEngDocumentByNumber(ses, prjCode, chkKeyCrrsOut);
                    this.log.info("Parent (CRRS) Doc ? " + prntDocument);
                    if(prntDocument != null){
                        ILink[] links = srv.getReferencedRelationships(ses, prntDocument, true, false);
                        for(ILink link : links){
                            linkList.add(link.getTargetDocumentId());
                        }
                    }
                    if (prntDocument != null && prntDocument.getDescriptorValue("ccmPrjDocCategory").trim().equalsIgnoreCase("TRANSMITTAL")) {
                        if (!Objects.equals(prntDocument.getID(), engDocument.getID()) && !linkList.contains(engDocument.getID())) {
                            ILink lnk2 = srv.createLink(ses, prntDocument.getID(), (INodeGeneric) null, engDocument.getID());
                            lnk2.commit();
                            engDocument.commit();
                            this.log.info("Created Link...");
                        }
                    }
                }
            }
        }catch (Exception e){
            throw new Exception("Exeption Caught..setParent: " + e);
        }
    }

    public IQueryDlg findQueryDlgForQueryClass(IQueryClass queryClass) {
        IQueryDlg dlg = null;
        if (queryClass != null) {
            dlg = queryClass.getQueryDlg("default");
        }

        return dlg;
    }
    public IQueryParameter query(ISession session, IQueryDlg queryDlg, Map<String, String> descriptorValues) {
        IDocumentServer documentServer = session.getDocumentServer();
        ISerClassFactory classFactory = documentServer.getClassFactory();
        IQueryParameter queryParameter = null;
        IQueryExpression expression = null;
        IComponent[] components = queryDlg.getComponents();

        for(int i = 0; i < components.length; ++i) {
            if (components[i].getType() == IMaskedEdit.TYPE) {
                IControl control = (IControl)components[i];
                String descriptorId = control.getDescriptorID();
                String value = (String)descriptorValues.get(descriptorId);
                if (value != null && value.trim().length() > 0) {
                    IDescriptor descriptor = documentServer.getDescriptor(descriptorId, session);
                    IQueryValueDescriptor queryValueDescriptor = classFactory.getQueryValueDescriptorInstance(descriptor);
                    queryValueDescriptor.addValue(value);
                    IQueryExpression expr = queryValueDescriptor.getExpression();
                    if (expression != null) {
                        expression = classFactory.getExpressionInstance(expression, expr, 0);
                    } else {
                        expression = expr;
                    }
                }
            }
        }

        if (expression != null) {
            queryParameter = classFactory.getQueryParameterInstance(session, queryDlg, expression);
        }

        return queryParameter;
    }
    public IDocumentHitList executeQuery(ISession session, IQueryParameter queryParameter) {
        IDocumentServer documentServer = session.getDocumentServer();
        return documentServer.query(queryParameter, session);
    }
    public IDocument getEngDocument(ISession session, String prjCode, String docKey) throws IOException {
        IDocument result = null;
        IDocumentServer documentServer = session.getDocumentServer();
        this.descriptor1 = documentServer.getDescriptorForName(session, "ccmPRJCard_code");
        this.descriptor2 = documentServer.getDescriptorForName(session, this.nameDescriptor1);
        IQueryClass queryClass = documentServer.getQueryClassByName(session, this.searchClassName);
        IQueryDlg queryDlg = this.findQueryDlgForQueryClass(queryClass);
        Map<String, String> searchDescriptors = new HashMap();
        searchDescriptors.put(this.descriptor1.getId(), prjCode);
        searchDescriptors.put(this.descriptor2.getId(), docKey);
        IQueryParameter queryParameter = this.query(session, queryDlg, searchDescriptors);
        if (queryParameter != null) {
            IDocumentHitList hitresult = this.executeQuery(session, queryParameter);
            IDocument[] hits = hitresult.getDocumentObjects();
            queryParameter.close();
            return hits != null && hits.length > 0 ? hits[0] : null;
        } else {
            return null;
        }
    }
    public IDocument getEngDocumentByNumber(ISession session, String prjCode, String docKey) {
        IDocument result = null;
        log.info("Search Eng Document By PRJ Code:" + prjCode);
        log.info("Search Eng Document By Number:" + docKey);
        IDocumentServer documentServer = session.getDocumentServer();
        this.descriptor1 = documentServer.getDescriptorForName(session, "ccmPRJCard_code");
        this.descriptor2 = documentServer.getDescriptorForName(session, "ccmPrjDocNumber");
        IQueryClass queryClass = documentServer.getQueryClassByName(session, this.searchClassName);
        IQueryDlg queryDlg = this.findQueryDlgForQueryClass(queryClass);
        Map<String, String> searchDescriptors = new HashMap();
        searchDescriptors.put(this.descriptor1.getId(), prjCode);
        searchDescriptors.put(this.descriptor2.getId(), docKey);
        IQueryParameter queryParameter = this.query(session, queryDlg, searchDescriptors);
        if (queryParameter != null) {
            IDocumentHitList hitresult = this.executeQuery(session, queryParameter);
            IDocument[] hits = hitresult.getDocumentObjects();
            queryParameter.close();
            return hits != null && hits.length > 0 ? hits[0] : null;
        } else {
            return null;
        }
    }
    public IDocument checkDublicateEngDocByFileName(IDocument doc1){
        IDocument result = null;
        ISession session = this.getSes();
        String searchClassName = "Search Engineering Documents";
        IDocumentServer documentServer = session.getDocumentServer();
        IDescriptor descriptor1 = documentServer.getDescriptorForName(session, "ccmPRJCard_code");
        IDescriptor descriptor2 = documentServer.getDescriptorForName(session, "ccmPrjDocFileName");
        IQueryClass queryClass = documentServer.getQueryClassByName(session, searchClassName);
        IQueryDlg queryDlg = this.findQueryDlgForQueryClass(queryClass);
        Map<String, String> searchDescriptors = new HashMap();
        searchDescriptors.put(descriptor1.getId(), doc1.getDescriptorValue("ccmPRJCard_code"));
        searchDescriptors.put(descriptor2.getId(), doc1.getDescriptorValue("ccmPrjDocFileName"));
        IQueryParameter queryParameter = this.query(session, queryDlg, searchDescriptors);
        if (queryParameter != null) {
            IDocumentHitList hitresult = this.executeQuery(session, queryParameter);
            IDocument[] hits = hitresult.getDocumentObjects();
            queryParameter.close();
            for(IDocument ldoc : hits){
                String docID = doc1.getID();
                String chkID = ldoc.getID();
                if(!Objects.equals(docID, chkID)){
                    result = ldoc;
                    break;
                }
            }
        }
        return result;
    }
    public static Object getValue(Cell cell, CellType type) {
        switch (type) {
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return getLocalDateTime(cell.getDateCellValue().toString());
                } else {
                    double value = cell.getNumericCellValue();
                    if (value == Math.floor(value)) {
                        return (long)value;
                    }

                    return value;
                }
            case STRING:
                return cell.getStringCellValue();
            case FORMULA:
                return getValue(cell, cell.getCachedFormulaResultType());
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case _NONE:
                return null;
            case BLANK:
                return null;
            case ERROR:
                return null;
            default:
                return null;
        }
    }
    public static LocalDateTime getLocalDateTime(String strDate) {
        strDate = strDate.replace("TRT", "Europe/Istanbul");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzzz yyyy");
        ZonedDateTime zdt = ZonedDateTime.parse(strDate, formatter);
        LocalDateTime ldt = zdt.toLocalDateTime();
        return ldt;
    }

}
