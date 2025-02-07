/*******************************************************************************
 * Copyright (c) 2009 Alexander Koderman <ak[at]sernet[dot]de>.
 * This program is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *     This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *     You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 *
 * Contributors:
 *     Alexander Koderman <ak[at]sernet[dot]de> - initial API and implementation
 ******************************************************************************/
package sernet.verinice.model.bsi;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import sernet.hui.common.connect.EntityType;
import sernet.hui.common.connect.HUITypeFactory;
import sernet.hui.common.connect.PropertyList;
import sernet.hui.common.connect.PropertyType;
import sernet.verinice.interfaces.IReevaluator;
import sernet.verinice.model.common.CascadingTransaction;
import sernet.verinice.model.common.CnALink;
import sernet.verinice.model.common.CnATreeElement;
import sernet.verinice.model.common.TransactionAbortedException;

/**
 * Adapter for elements that provide or receive protection levels.
 *
 * @author koderman[at]sernet[dot]de
 * @version $Rev$ $LastChangedDate$ $LastChangedBy$
 *
 */
@SuppressWarnings("serial")
public class ProtectionRequirementsAdapter implements IReevaluator, Serializable {

    private static final Logger log = Logger.getLogger(ProtectionRequirementsAdapter.class);

    private CnATreeElement cnaTreeElement;

    public ProtectionRequirementsAdapter(CnATreeElement parent) {
        this.cnaTreeElement = parent;
    }

    @Override
    public int getIntegrity() {
        return getSelectedOptionAsNumericValue(
                cnaTreeElement.getTypeId() + Schutzbedarf.INTEGRITAET);
    }

    @Override
    public int getAvailability() {
        return getSelectedOptionAsNumericValue(
                cnaTreeElement.getTypeId() + Schutzbedarf.VERFUEGBARKEIT);
    }

    @Override
    public int getConfidentiality() {
        return getSelectedOptionAsNumericValue(
                cnaTreeElement.getTypeId() + Schutzbedarf.VERTRAULICHKEIT);
    }

    public static boolean hasValue(PropertyList properties) {
        return properties != null && !properties.getProperties().isEmpty()
                && properties.getProperty(0).getPropertyValue() != null;
    }

    private int getSelectedOptionAsNumericValue(String propertyId) {
        PropertyList properties = cnaTreeElement.getEntity().getProperties(propertyId);
        if (hasValue(properties)) {
            String value = properties.getProperty(0).getPropertyValue();
            if (!value.isEmpty()) {
                return Schutzbedarf.toInt(cnaTreeElement.getTypeId(), propertyId, value);
            }
        }
        return Schutzbedarf.UNDEF;
    }

    @Override
    public void setIntegrity(int i) {
        setSelectedOptionFromNumericValue(cnaTreeElement.getTypeId() + Schutzbedarf.INTEGRITAET, i);
    }

    @Override
    public void setAvailability(int i) {
        setSelectedOptionFromNumericValue(cnaTreeElement.getTypeId() + Schutzbedarf.VERFUEGBARKEIT,
                i);
    }

    @Override
    public void setConfidentiality(int i) {
        setSelectedOptionFromNumericValue(cnaTreeElement.getTypeId() + Schutzbedarf.VERTRAULICHKEIT,
                i);
    }

    private void setSelectedOptionFromNumericValue(String propertyId, int numericValue) {
        String optionId = null;
        EntityType entityType = HUITypeFactory.getInstance()
                .getEntityType(cnaTreeElement.getEntity().getEntityType());
        PropertyType propertyType = entityType.getPropertyType(propertyId);
        if (numericValue != Schutzbedarf.UNDEF) {
            optionId = propertyType.getOptions().get(numericValue - 1).getId();
        }
        cnaTreeElement.getEntity().setSimpleValue(propertyType, optionId);
    }

    @Override
    public String getIntegrityDescription() {
        return cnaTreeElement.getEntity()
                .getSimpleValue(cnaTreeElement.getTypeId() + Schutzbedarf.INTEGRITAET_BEGRUENDUNG);
    }

    @Override
    public String getAvailabilityDescription() {
        return cnaTreeElement.getEntity().getSimpleValue(
                cnaTreeElement.getTypeId() + Schutzbedarf.VERFUEGBARKEIT_BEGRUENDUNG);
    }

    @Override
    public String getConfidentialityDescription() {
        return cnaTreeElement.getEntity().getSimpleValue(
                cnaTreeElement.getTypeId() + Schutzbedarf.VERTRAULICHKEIT_BEGRUENDUNG);
    }

    @Override
    public void setIntegrityDescription(String text) {
        EntityType entityType = HUITypeFactory.getInstance()
                .getEntityType(cnaTreeElement.getEntity().getEntityType());
        cnaTreeElement.getEntity().setSimpleValue(entityType.getPropertyType(
                cnaTreeElement.getTypeId() + Schutzbedarf.INTEGRITAET_BEGRUENDUNG), text);
    }

    @Override
    public void setAvailabilityDescription(String text) {
        EntityType entityType = HUITypeFactory.getInstance()
                .getEntityType(cnaTreeElement.getEntity().getEntityType());
        cnaTreeElement.getEntity()
                .setSimpleValue(entityType.getPropertyType(
                        cnaTreeElement.getTypeId() + Schutzbedarf.VERFUEGBARKEIT_BEGRUENDUNG),
                        text);
    }

    @Override
    public void setConfidentialityDescription(String text) {
        EntityType entityType = HUITypeFactory.getInstance()
                .getEntityType(cnaTreeElement.getEntity().getEntityType());
        cnaTreeElement.getEntity()
                .setSimpleValue(entityType.getPropertyType(
                        cnaTreeElement.getTypeId() + Schutzbedarf.VERTRAULICHKEIT_BEGRUENDUNG),
                        text);
    }

    private void fireVerfuegbarkeitChanged(CascadingTransaction ta) {

        try {
            // 1st step: traverse down:
            // find bottom nodes from which to start:
            CascadingTransaction downwardsTA = new CascadingTransaction();
            Set<CnATreeElement> bottomNodes = new HashSet<>();
            findBottomNodes(cnaTreeElement, bottomNodes, downwardsTA);

            // 2nd step: traverse up:
            for (CnATreeElement bottomNode : bottomNodes) {
                // determine protection level from parents (or keep own
                // depending on description):
                bottomNode.getLinkChangeListener().determineAvailability(ta);

            }

        } catch (TransactionAbortedException tae) {
            log.debug("Reeavluation of availability aborted."); //$NON-NLS-1$
            throw new RuntimeException(tae);
        } catch (RuntimeException e) {
            ta.abort();
            throw e;
        } catch (Exception e) {
            ta.abort();
            throw new RuntimeException(e);
        }
    }

    private void fireVertraulichkeitChanged(CascadingTransaction ta) {

        try {
            // 1st step: traverse down:
            // find bottom nodes from which to start:
            CascadingTransaction downwardsTA = new CascadingTransaction();
            Set<CnATreeElement> bottomNodes = new HashSet<>();
            findBottomNodes(cnaTreeElement, bottomNodes, downwardsTA);

            // 2nd step: traverse up:
            for (CnATreeElement bottomNode : bottomNodes) {
                // determine protection level from parents (or keep own
                // depending on description):
                bottomNode.getLinkChangeListener().determineConfidentiality(ta);

            }

        } catch (TransactionAbortedException tae) {
            log.debug("Reeavluation of confidentiality aborted."); //$NON-NLS-1$
            throw new RuntimeException(tae);
        } catch (RuntimeException e) {
            ta.abort();
            throw e;
        } catch (Exception e) {
            ta.abort();
            throw new RuntimeException(e);
        }
    }

    private void fireIntegritaetChanged(CascadingTransaction ta) {

        try {
            // 1st step: traverse down:
            // find bottom nodes from which to start:
            CascadingTransaction downwardsTA = new CascadingTransaction();
            Set<CnATreeElement> bottomNodes = new HashSet<>();
            findBottomNodes(cnaTreeElement, bottomNodes, downwardsTA);

            // 2nd step: traverse up:
            for (CnATreeElement bottomNode : bottomNodes) {
                // determine protection level from parents (or keep own
                // depending on description):
                bottomNode.getLinkChangeListener().determineIntegrity(ta);

            }

        } catch (TransactionAbortedException tae) {
            log.debug("Reeavluation of integrity aborted."); //$NON-NLS-1$
            throw new RuntimeException(tae);
        } catch (RuntimeException e) {
            ta.abort();
            throw e;
        } catch (Exception e) {
            ta.abort();
            throw new RuntimeException(e);
        }
    }

    /**
     * @param downwardElement
     * @param downwardsTA
     * @param bottomNodes
     * @return
     */
    private void findBottomNodes(CnATreeElement downwardElement, Set<CnATreeElement> bottomNodes,
            CascadingTransaction downwardsTA) {
        if (downwardsTA.hasBeenVisited(downwardElement)) {
            return;
        }

        try {
            downwardsTA.enter(downwardElement);
        } catch (TransactionAbortedException e) {
            Logger.getLogger(this.getClass())
                    .error(Messages.SchutzbedarfAdapter_3 + downwardElement.getTitle(), e);
            return;
        }

        int countLinks = 0;
        for (CnALink link : downwardElement.getLinksDown()) {
            if (link.getDependency().isProtectionRequirementsProvider()) {
                countLinks++;
                findBottomNodes(link.getDependency(), bottomNodes, downwardsTA);
            }
        }

        // could not go further down, so add this node:
        if (countLinks == 0) {
            bottomNodes.add(downwardElement);
        }
    }

    public CnATreeElement getParent() {
        return cnaTreeElement;
    }

    public void setParent(CnATreeElement parent) {
        this.cnaTreeElement = parent;
    }

    @Override
    public void updateIntegrity(CascadingTransaction ta) {
        fireIntegritaetChanged(ta);
    }

    @Override
    public void updateAvailability(CascadingTransaction ta) {
        fireVerfuegbarkeitChanged(ta);
    }

    @Override
    public void updateConfidentiality(CascadingTransaction ta) {
        fireVertraulichkeitChanged(ta);
    }

    /*
     * (non-Javadoc)
     *
     * @seesernet.gs.ui.rcp.main.bsi.model.ISchutzbedarfProvider#
     * isCalculatedAvailability()
     */
    @Override
    public boolean isCalculatedAvailability() {
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @seesernet.gs.ui.rcp.main.bsi.model.ISchutzbedarfProvider#
     * isCalculatedConfidentiality()
     */
    @Override
    public boolean isCalculatedConfidentiality() {
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see sernet.gs.ui.rcp.main.bsi.model.ISchutzbedarfProvider#
     * isCalculatedIntegrity ()
     */
    @Override
    public boolean isCalculatedIntegrity() {
        return false;
    }
}
