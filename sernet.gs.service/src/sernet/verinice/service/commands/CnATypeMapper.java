/*******************************************************************************
 * Copyright (c) 2010 Alexander Koderman <ak@sernet.de>.
 * This program is free software: you can redistribute it and/or 
 * modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation, either version 3 
 * of the License, or (at your option) any later version.
 *     This program is distributed in the hope that it will be useful,    
 * but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * See the GNU General Public License for more details.
 *     You should have received a copy of the GNU General Public 
 * License along with this program. 
 * If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Alexander Koderman <ak@sernet.de> - initial API and implementation
 ******************************************************************************/
package sernet.verinice.service.commands;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.bidimap.DualHashBidiMap;

import sernet.hui.common.connect.EntityType;
import sernet.verinice.model.bp.IBpElement;
import sernet.verinice.model.bp.elements.Application;
import sernet.verinice.model.bp.elements.BpDocument;
import sernet.verinice.model.bp.elements.BpIncident;
import sernet.verinice.model.bp.elements.BpPerson;
import sernet.verinice.model.bp.elements.BpRecord;
import sernet.verinice.model.bp.elements.BpRequirement;
import sernet.verinice.model.bp.elements.BpThreat;
import sernet.verinice.model.bp.elements.BusinessProcess;
import sernet.verinice.model.bp.elements.Device;
import sernet.verinice.model.bp.elements.IcsSystem;
import sernet.verinice.model.bp.elements.ItNetwork;
import sernet.verinice.model.bp.elements.ItSystem;
import sernet.verinice.model.bp.elements.Network;
import sernet.verinice.model.bp.elements.Room;
import sernet.verinice.model.bp.elements.Safeguard;
import sernet.verinice.model.bp.groups.ApplicationGroup;
import sernet.verinice.model.bp.groups.BpDocumentGroup;
import sernet.verinice.model.bp.groups.BpIncidentGroup;
import sernet.verinice.model.bp.groups.BpPersonGroup;
import sernet.verinice.model.bp.groups.BpRecordGroup;
import sernet.verinice.model.bp.groups.BpRequirementGroup;
import sernet.verinice.model.bp.groups.BpThreatGroup;
import sernet.verinice.model.bp.groups.BusinessProcessGroup;
import sernet.verinice.model.bp.groups.DeviceGroup;
import sernet.verinice.model.bp.groups.IcsSystemGroup;
import sernet.verinice.model.bp.groups.ItSystemGroup;
import sernet.verinice.model.bp.groups.NetworkGroup;
import sernet.verinice.model.bp.groups.RoomGroup;
import sernet.verinice.model.bp.groups.SafeguardGroup;
import sernet.verinice.model.bsi.Anwendung;
import sernet.verinice.model.bsi.AnwendungenKategorie;
import sernet.verinice.model.bsi.BSIModel;
import sernet.verinice.model.bsi.BausteinUmsetzung;
import sernet.verinice.model.bsi.Client;
import sernet.verinice.model.bsi.ClientsKategorie;
import sernet.verinice.model.bsi.Gebaeude;
import sernet.verinice.model.bsi.GebaeudeKategorie;
import sernet.verinice.model.bsi.IBSIStrukturElement;
import sernet.verinice.model.bsi.IBSIStrukturKategorie;
import sernet.verinice.model.bsi.IMassnahmeUmsetzung;
import sernet.verinice.model.bsi.ITVerbund;
import sernet.verinice.model.bsi.MassnahmeKategorie;
import sernet.verinice.model.bsi.MassnahmenUmsetzung;
import sernet.verinice.model.bsi.NKKategorie;
import sernet.verinice.model.bsi.NetzKomponente;
import sernet.verinice.model.bsi.Person;
import sernet.verinice.model.bsi.PersonenKategorie;
import sernet.verinice.model.bsi.RaeumeKategorie;
import sernet.verinice.model.bsi.Raum;
import sernet.verinice.model.bsi.Server;
import sernet.verinice.model.bsi.ServerKategorie;
import sernet.verinice.model.bsi.SonstIT;
import sernet.verinice.model.bsi.SonstigeITKategorie;
import sernet.verinice.model.bsi.TKKategorie;
import sernet.verinice.model.bsi.TelefonKomponente;
import sernet.verinice.model.bsi.risikoanalyse.FinishedRiskAnalysis;
import sernet.verinice.model.bsi.risikoanalyse.GefaehrdungsUmsetzung;
import sernet.verinice.model.bsi.risikoanalyse.IGefaehrdungsBaumElement;
import sernet.verinice.model.catalog.CatalogModel;
import sernet.verinice.model.common.CnATreeElement;
import sernet.verinice.model.common.Domain;
import sernet.verinice.model.ds.Datenverarbeitung;
import sernet.verinice.model.ds.IDatenschutzElement;
import sernet.verinice.model.ds.Personengruppen;
import sernet.verinice.model.ds.StellungnahmeDSB;
import sernet.verinice.model.ds.VerantwortlicheStelle;
import sernet.verinice.model.ds.Verarbeitungsangaben;
import sernet.verinice.model.ds.Zweckbestimmung;
import sernet.verinice.model.iso27k.Asset;
import sernet.verinice.model.iso27k.AssetGroup;
import sernet.verinice.model.iso27k.Audit;
import sernet.verinice.model.iso27k.AuditGroup;
import sernet.verinice.model.iso27k.Control;
import sernet.verinice.model.iso27k.ControlGroup;
import sernet.verinice.model.iso27k.Document;
import sernet.verinice.model.iso27k.DocumentGroup;
import sernet.verinice.model.iso27k.Evidence;
import sernet.verinice.model.iso27k.EvidenceGroup;
import sernet.verinice.model.iso27k.ExceptionGroup;
import sernet.verinice.model.iso27k.Finding;
import sernet.verinice.model.iso27k.FindingGroup;
import sernet.verinice.model.iso27k.Group;
import sernet.verinice.model.iso27k.IISO27kElement;
import sernet.verinice.model.iso27k.Incident;
import sernet.verinice.model.iso27k.IncidentGroup;
import sernet.verinice.model.iso27k.IncidentScenario;
import sernet.verinice.model.iso27k.IncidentScenarioGroup;
import sernet.verinice.model.iso27k.Interview;
import sernet.verinice.model.iso27k.InterviewGroup;
import sernet.verinice.model.iso27k.Organization;
import sernet.verinice.model.iso27k.PersonGroup;
import sernet.verinice.model.iso27k.PersonIso;
import sernet.verinice.model.iso27k.ProcessGroup;
import sernet.verinice.model.iso27k.Record;
import sernet.verinice.model.iso27k.RecordGroup;
import sernet.verinice.model.iso27k.Requirement;
import sernet.verinice.model.iso27k.RequirementGroup;
import sernet.verinice.model.iso27k.Response;
import sernet.verinice.model.iso27k.ResponseGroup;
import sernet.verinice.model.iso27k.Threat;
import sernet.verinice.model.iso27k.ThreatGroup;
import sernet.verinice.model.iso27k.Vulnerability;
import sernet.verinice.model.iso27k.VulnerabilityGroup;
import sernet.verinice.model.samt.SamtTopic;

/**
 * Mapper to find classes by typeID String. Needed during Import (originally
 * refactored out of SyncInsertUpdateCommand) but also needed in many other
 * cases because Hibernate looses marker interfaces (such as IISO27kElement)
 * when loading subclasses over generic queries.
 * 
 * @author koderman@sernet.de
 * @version $Rev$ $LastChangedDate$ $LastChangedBy$
 *
 */
public final class CnATypeMapper {
    private static final Map<String, Class<? extends CnATreeElement>> typeIdClass = new HashMap<>();

    private static final Map<String, String> descriptionPropertyMap = new HashMap<>();

    private static final BidiMap elementTypeIdToGroupTypeId = new DualHashBidiMap();

    static {
        typeIdClass.put(Anwendung.TYPE_ID, Anwendung.class);
        typeIdClass.put(Gebaeude.TYPE_ID, Gebaeude.class);
        typeIdClass.put(Client.TYPE_ID, Client.class);
        typeIdClass.put(Server.TYPE_ID, Server.class);
        typeIdClass.put(SonstIT.TYPE_ID, SonstIT.class);
        typeIdClass.put(TelefonKomponente.TYPE_ID, TelefonKomponente.class);
        typeIdClass.put(Person.TYPE_ID, Person.class);
        typeIdClass.put(NetzKomponente.TYPE_ID, NetzKomponente.class);
        typeIdClass.put(Raum.TYPE_ID, Raum.class);
        typeIdClass.put(AnwendungenKategorie.TYPE_ID, AnwendungenKategorie.class);
        typeIdClass.put(GebaeudeKategorie.TYPE_ID, GebaeudeKategorie.class);
        typeIdClass.put(ClientsKategorie.TYPE_ID, ClientsKategorie.class);
        typeIdClass.put(ServerKategorie.TYPE_ID, ServerKategorie.class);
        typeIdClass.put(SonstigeITKategorie.TYPE_ID, SonstigeITKategorie.class);
        typeIdClass.put(TKKategorie.TYPE_ID, TKKategorie.class);
        typeIdClass.put(PersonenKategorie.TYPE_ID, PersonenKategorie.class);
        typeIdClass.put(NKKategorie.TYPE_ID, NKKategorie.class);
        typeIdClass.put(RaeumeKategorie.TYPE_ID, RaeumeKategorie.class);
        typeIdClass.put(BausteinUmsetzung.TYPE_ID, BausteinUmsetzung.class);
        typeIdClass.put(ITVerbund.TYPE_ID, ITVerbund.class);
        typeIdClass.put(MassnahmeKategorie.TYPE_ID, MassnahmeKategorie.class);
        typeIdClass.put(MassnahmenUmsetzung.TYPE_ID, MassnahmenUmsetzung.class);
        typeIdClass.put(Verarbeitungsangaben.TYPE_ID, Verarbeitungsangaben.class);
        typeIdClass.put(Personengruppen.TYPE_ID, Personengruppen.class);
        typeIdClass.put(VerantwortlicheStelle.TYPE_ID, VerantwortlicheStelle.class);
        typeIdClass.put(StellungnahmeDSB.TYPE_ID, StellungnahmeDSB.class);
        typeIdClass.put(Datenverarbeitung.TYPE_ID, Datenverarbeitung.class);
        typeIdClass.put(Zweckbestimmung.TYPE_ID, Zweckbestimmung.class);

        typeIdClass.put(ResponseGroup.TYPE_ID, ResponseGroup.class);
        typeIdClass.put(ExceptionGroup.TYPE_ID, ExceptionGroup.class);
        typeIdClass.put(VulnerabilityGroup.TYPE_ID, VulnerabilityGroup.class);
        typeIdClass.put(PersonGroup.TYPE_ID, PersonGroup.class);
        typeIdClass.put(IncidentGroup.TYPE_ID, IncidentGroup.class);
        typeIdClass.put(ThreatGroup.TYPE_ID, ThreatGroup.class);
        typeIdClass.put(Organization.TYPE_ID, Organization.class);
        typeIdClass.put(ProcessGroup.TYPE_ID, ProcessGroup.class);
        typeIdClass.put(AuditGroup.TYPE_ID, AuditGroup.class);
        typeIdClass.put(IncidentScenarioGroup.TYPE_ID, IncidentScenarioGroup.class);
        typeIdClass.put(RecordGroup.TYPE_ID, RecordGroup.class);
        typeIdClass.put(RequirementGroup.TYPE_ID, RequirementGroup.class);
        typeIdClass.put(ControlGroup.TYPE_ID, ControlGroup.class);
        typeIdClass.put(DocumentGroup.TYPE_ID, DocumentGroup.class);
        typeIdClass.put(AssetGroup.TYPE_ID, AssetGroup.class);
        typeIdClass.put(EvidenceGroup.TYPE_ID, EvidenceGroup.class);
        typeIdClass.put(InterviewGroup.TYPE_ID, InterviewGroup.class);
        typeIdClass.put(FindingGroup.TYPE_ID, FindingGroup.class);

        typeIdClass.put(Response.TYPE_ID, Response.class);
        typeIdClass.put(sernet.verinice.model.iso27k.Exception.TYPE_ID,
                sernet.verinice.model.iso27k.Exception.class);
        typeIdClass.put(Vulnerability.TYPE_ID, Vulnerability.class);
        typeIdClass.put(PersonIso.TYPE_ID, PersonIso.class);
        typeIdClass.put(Incident.TYPE_ID, Incident.class);
        typeIdClass.put(Threat.TYPE_ID, Threat.class);
        typeIdClass.put(sernet.verinice.model.iso27k.Process.TYPE_ID,
                sernet.verinice.model.iso27k.Process.class);
        typeIdClass.put(Audit.TYPE_ID, Audit.class);
        typeIdClass.put(IncidentScenario.TYPE_ID, IncidentScenario.class);
        typeIdClass.put(Record.TYPE_ID, Record.class);
        typeIdClass.put(Requirement.TYPE_ID, Requirement.class);
        typeIdClass.put(Control.TYPE_ID, Control.class);
        typeIdClass.put(Document.TYPE_ID, Document.class);
        typeIdClass.put(Asset.TYPE_ID, Asset.class);
        typeIdClass.put(Evidence.TYPE_ID, Evidence.class);
        typeIdClass.put(Interview.TYPE_ID, Interview.class);
        typeIdClass.put(Finding.TYPE_ID, Finding.class);

        typeIdClass.put(SamtTopic.TYPE_ID, SamtTopic.class);

        typeIdClass.put(GefaehrdungsUmsetzung.TYPE_ID, GefaehrdungsUmsetzung.class);
        typeIdClass.put(FinishedRiskAnalysis.TYPE_ID, FinishedRiskAnalysis.class);

        typeIdClass.put(ApplicationGroup.TYPE_ID, ApplicationGroup.class);
        typeIdClass.put(BpPersonGroup.TYPE_ID, BpPersonGroup.class);
        typeIdClass.put(BpRequirementGroup.TYPE_ID, BpRequirementGroup.class);
        typeIdClass.put(BpThreatGroup.TYPE_ID, BpThreatGroup.class);
        typeIdClass.put(BusinessProcessGroup.TYPE_ID, BusinessProcessGroup.class);
        typeIdClass.put(DeviceGroup.TYPE_ID, DeviceGroup.class);
        typeIdClass.put(IcsSystemGroup.TYPE_ID, IcsSystemGroup.class);
        typeIdClass.put(ItSystemGroup.TYPE_ID, ItSystemGroup.class);
        typeIdClass.put(NetworkGroup.TYPE_ID, NetworkGroup.class);
        typeIdClass.put(RoomGroup.TYPE_ID, RoomGroup.class);
        typeIdClass.put(SafeguardGroup.TYPE_ID, SafeguardGroup.class);
        typeIdClass.put(BpDocumentGroup.TYPE_ID, BpDocumentGroup.class);
        typeIdClass.put(BpIncidentGroup.TYPE_ID, BpIncidentGroup.class);
        typeIdClass.put(BpRecordGroup.TYPE_ID, BpRecordGroup.class);

        typeIdClass.put(Application.TYPE_ID, Application.class);
        typeIdClass.put(BpPerson.TYPE_ID, BpPerson.class);
        typeIdClass.put(BpThreat.TYPE_ID, BpThreat.class);
        typeIdClass.put(BpRequirement.TYPE_ID, BpRequirement.class);
        typeIdClass.put(BusinessProcess.TYPE_ID, BusinessProcess.class);
        typeIdClass.put(Device.TYPE_ID, Device.class);
        typeIdClass.put(IcsSystem.TYPE_ID, IcsSystem.class);
        typeIdClass.put(ItNetwork.TYPE_ID, ItNetwork.class);
        typeIdClass.put(ItSystem.TYPE_ID, ItSystem.class);
        typeIdClass.put(Network.TYPE_ID, Network.class);
        typeIdClass.put(Room.TYPE_ID, Room.class);
        typeIdClass.put(Safeguard.TYPE_ID, Safeguard.class);
        typeIdClass.put(CatalogModel.TYPE_ID, CatalogModel.class);
        typeIdClass.put(BpDocument.TYPE_ID, BpDocument.class);
        typeIdClass.put(BpIncident.TYPE_ID, BpIncident.class);
        typeIdClass.put(BpRecord.TYPE_ID, BpRecord.class);

        // map for description properties:
        descriptionPropertyMap.put(Client.TYPE_ID, Client.PROP_ERLAEUTERUNG);
        descriptionPropertyMap.put(Gebaeude.TYPE_ID, Gebaeude.PROP_ERLAEUTERUNG);
        descriptionPropertyMap.put(Server.TYPE_ID, Server.PROP_ERLAEUTERUNG);
        descriptionPropertyMap.put(SonstIT.TYPE_ID, SonstIT.PROP_ERLAEUTERUNG);
        descriptionPropertyMap.put(TelefonKomponente.TYPE_ID, TelefonKomponente.PROP_ERLAEUTERUNG);
        descriptionPropertyMap.put(Person.TYPE_ID, Person.PROP_ERLAEUTERUNG);
        descriptionPropertyMap.put(NetzKomponente.TYPE_ID, NetzKomponente.PROP_ERLAEUTERUNG);
        descriptionPropertyMap.put(Raum.TYPE_ID, Raum.PROP_ERLAEUTERUNG);
        descriptionPropertyMap.put(BausteinUmsetzung.TYPE_ID, BausteinUmsetzung.P_ERLAEUTERUNG);
        descriptionPropertyMap.put(MassnahmenUmsetzung.TYPE_ID, MassnahmenUmsetzung.P_ERLAEUTERUNG);

        // ISM
        elementTypeIdToGroupTypeId.put(Asset.TYPE_ID, AssetGroup.TYPE_ID);
        elementTypeIdToGroupTypeId.put(Audit.TYPE_ID, AuditGroup.TYPE_ID);
        elementTypeIdToGroupTypeId.put(Control.TYPE_ID, ControlGroup.TYPE_ID);
        elementTypeIdToGroupTypeId.put(Document.TYPE_ID, DocumentGroup.TYPE_ID);
        elementTypeIdToGroupTypeId.put(Evidence.TYPE_ID, EvidenceGroup.TYPE_ID);
        elementTypeIdToGroupTypeId.put(sernet.verinice.model.iso27k.Exception.TYPE_ID,
                ExceptionGroup.TYPE_ID);
        elementTypeIdToGroupTypeId.put(Finding.TYPE_ID, FindingGroup.TYPE_ID);
        elementTypeIdToGroupTypeId.put(Incident.TYPE_ID, IncidentGroup.TYPE_ID);
        elementTypeIdToGroupTypeId.put(IncidentScenario.TYPE_ID, IncidentScenarioGroup.TYPE_ID);
        elementTypeIdToGroupTypeId.put(Interview.TYPE_ID, InterviewGroup.TYPE_ID);
        elementTypeIdToGroupTypeId.put(PersonIso.TYPE_ID, PersonGroup.TYPE_ID);
        elementTypeIdToGroupTypeId.put(sernet.verinice.model.iso27k.Process.TYPE_ID,
                ProcessGroup.TYPE_ID);
        elementTypeIdToGroupTypeId.put(Record.TYPE_ID, RecordGroup.TYPE_ID);
        elementTypeIdToGroupTypeId.put(Requirement.TYPE_ID, RequirementGroup.TYPE_ID);
        elementTypeIdToGroupTypeId.put(Response.TYPE_ID, ResponseGroup.TYPE_ID);
        elementTypeIdToGroupTypeId.put(SamtTopic.TYPE_ID, ControlGroup.TYPE_ID);
        elementTypeIdToGroupTypeId.put(Threat.TYPE_ID, ThreatGroup.TYPE_ID);
        elementTypeIdToGroupTypeId.put(Vulnerability.TYPE_ID, VulnerabilityGroup.TYPE_ID);

        // modernized BP
        elementTypeIdToGroupTypeId.put(Application.TYPE_ID, ApplicationGroup.TYPE_ID);
        elementTypeIdToGroupTypeId.put(BpPerson.TYPE_ID, BpPersonGroup.TYPE_ID);
        elementTypeIdToGroupTypeId.put(BpThreat.TYPE_ID, BpThreatGroup.TYPE_ID);
        elementTypeIdToGroupTypeId.put(BpRequirement.TYPE_ID, BpRequirementGroup.TYPE_ID);
        elementTypeIdToGroupTypeId.put(BusinessProcess.TYPE_ID, BusinessProcessGroup.TYPE_ID);
        elementTypeIdToGroupTypeId.put(Device.TYPE_ID, DeviceGroup.TYPE_ID);
        elementTypeIdToGroupTypeId.put(IcsSystem.TYPE_ID, IcsSystemGroup.TYPE_ID);
        elementTypeIdToGroupTypeId.put(ItSystem.TYPE_ID, ItSystemGroup.TYPE_ID);
        elementTypeIdToGroupTypeId.put(Network.TYPE_ID, NetworkGroup.TYPE_ID);
        elementTypeIdToGroupTypeId.put(Room.TYPE_ID, RoomGroup.TYPE_ID);
        elementTypeIdToGroupTypeId.put(Safeguard.TYPE_ID, SafeguardGroup.TYPE_ID);
        elementTypeIdToGroupTypeId.put(BpDocument.TYPE_ID, BpDocumentGroup.TYPE_ID);
        elementTypeIdToGroupTypeId.put(BpIncident.TYPE_ID, BpIncidentGroup.TYPE_ID);
        elementTypeIdToGroupTypeId.put(BpRecord.TYPE_ID, BpRecordGroup.TYPE_ID);
    }

    // this is necessary because hibernate returns proxy objects that will not
    // implement the marker interface IBSIStrukturelement
    public static final List<String> STRUKTUR_ELEMENT_TYPES = Collections
            .unmodifiableList(Arrays.asList(Anwendung.TYPE_ID, BSIModel.TYPE_ID, Client.TYPE_ID,
                    Gebaeude.TYPE_ID, ITVerbund.TYPE_ID, NetzKomponente.TYPE_ID, Person.TYPE_ID,
                    Raum.TYPE_ID, Server.TYPE_ID, SonstIT.TYPE_ID, TelefonKomponente.TYPE_ID));

    public static final List<String> BP_ELEMENT_TYPES = Collections
            .unmodifiableList(Arrays.asList(BusinessProcessGroup.TYPE_ID, ApplicationGroup.TYPE_ID,
                    ItSystemGroup.TYPE_ID, IcsSystemGroup.TYPE_ID, DeviceGroup.TYPE_ID,
                    NetworkGroup.TYPE_ID, RoomGroup.TYPE_ID, BpPersonGroup.TYPE_ID,
                    BpRequirementGroup.TYPE_ID, BpThreatGroup.TYPE_ID, SafeguardGroup.TYPE_ID,
                    BpDocumentGroup.TYPE_ID, BpIncidentGroup.TYPE_ID, BpRecordGroup.TYPE_ID));

    public static boolean isStrukturElement(CnATreeElement child) {
        EntityType entityType = child.getEntityType();
        if (entityType == null) {
            return false;
        }
        return STRUKTUR_ELEMENT_TYPES.contains(entityType.getId());
    }

    public static boolean isBpElement(CnATreeElement child) {
        EntityType entityType = child.getEntityType();
        if (entityType == null) {
            return false;
        }
        return BP_ELEMENT_TYPES.contains(entityType.getId());
    }

    /************************************************************
     * getClassFromTypeId()
     * 
     * @param typeId
     * @return the corresponding Class
     ************************************************************/
    @SuppressWarnings("unchecked")
    public static <T> Class<T> getClassFromTypeId(String typeId) {
        Class<T> klass = (Class<T>) typeIdClass.get(typeId);
        if (klass == null) {
            throw new IllegalStateException(
                    String.format("Type ID '%s' was not available in type map.", typeId));
        }

        return klass;
    }

    public static Domain getDomainFromTypeId(String typeId) {
        Class<Object> clazz = getClassFromTypeId(typeId);
        if (IBpElement.class.isAssignableFrom(clazz)) {
            return Domain.BASE_PROTECTION;
        }
        if (IISO27kElement.class.isAssignableFrom(clazz)) {
            return Domain.ISM;
        }
        if (IBSIStrukturElement.class.isAssignableFrom(clazz)
                || IBSIStrukturKategorie.class.isAssignableFrom(clazz)
                || IMassnahmeUmsetzung.class.isAssignableFrom(clazz)
                || IGefaehrdungsBaumElement.class.isAssignableFrom(clazz)
                || BausteinUmsetzung.class.equals(clazz)
                || FinishedRiskAnalysis.class.equals(clazz)) {
            return Domain.BASE_PROTECTION_OLD;
        }
        if (IDatenschutzElement.class.isAssignableFrom(clazz)) {
            return Domain.DATA_PROTECTION;
        }
        throw new IllegalArgumentException("Unupported type " + clazz + " (" + typeId + ")");
    }

    public static String getDescriptionPropertyForType(String typeId) {
        return descriptionPropertyMap.get(typeId);
    }

    public static String getGroupTypeIdFromElementTypeId(String typeId) {
        return (String) Optional.ofNullable(elementTypeIdToGroupTypeId.get(typeId))
                .orElseThrow(IllegalArgumentException::new);
    }

    public static String getElementTypeIdFromGroupTypeId(String typeId) {
        return (String) Optional.ofNullable(elementTypeIdToGroupTypeId.getKey(typeId))
                .orElseThrow(IllegalArgumentException::new);
    }

    public static boolean isGroupTypeId(String typeId) {
        return Group.class.isAssignableFrom(CnATypeMapper.getClassFromTypeId(typeId));
    }

    public static boolean isScopeType(String typeId) {
        return Organization.TYPE_ID.equals(typeId) || ITVerbund.TYPE_ID.equals(typeId) || ItNetwork.TYPE_ID.equals(typeId);
    }

    private CnATypeMapper() {

    }
}
