package org.xmlcml.cml.chemdraw;

import java.util.List;

import nu.xom.Attribute;
import nu.xom.Nodes;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.base.CMLElements;
import org.xmlcml.cml.element.CMLLabel;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLProduct;
import org.xmlcml.cml.element.CMLProductList;
import org.xmlcml.cml.element.CMLReactant;
import org.xmlcml.cml.element.CMLReactantList;
import org.xmlcml.cml.element.CMLReaction;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.euclid.EuclidRuntimeException;
import org.xmlcml.euclid.Real2Range;
/**
 * @author pm286
 *
 */
public class ChemDrawReactionConverter implements CDXConstants {
	
	static Logger LOG = Logger.getLogger(ChemDrawReactionConverter.class);
	static {
		LOG.setLevel(Level.INFO);
	}

	// FIXME to make it compile
	CMLElement scopeElement = null;
	CMLElement reaction = null;
	CMLProductList productList;
	CMLReactantList reactantList;
	
	
	/** constructor
	 * @param chemDrawConverter
	 * @param reaction
	 * @param scopeElement
	 */
	public ChemDrawReactionConverter(CMLReaction reaction, CMLElement scopeElement) {
		this.reaction = reaction;
		LOG.debug("REACTION "+reaction);
		this.scopeElement = scopeElement;
	}
	
	public void processAfterParsing() {
		addMoleculeRefsToReactants(reactantList);
		addMoleculeRefsToProducts(productList);
		this.processReactionStepArrowsAndText();
	}

	/**
	 * @param parent
	 * @param reactionStepArrows
	 * @throws EuclidRuntimeException
	 * @throws NumberFormatException
	 * @throws RuntimeException
	 */
	void processReactionStepArrowsAndText() {
		// will use attributes on reaction later
//		process(parent);
		String reactionStepArrows = reaction.getAttributeValue("ReactionStepArrows", CDX_NAMESPACE);
		if (reactionStepArrows != null) {
			reactionStepArrows = reactionStepArrows.trim();
			if (reactionStepArrows.indexOf(S_SPACE) != -1) {
				LOG.warn("Cannot yet deal with multiple reaction step arrows");
			} else {
				Nodes nodes = scopeElement.query("./cml:scalar[@id='"+reactionStepArrows+"']", CML_XPATH);
				if (nodes.size() == 1) {
					CMLScalar arrow = (CMLScalar) nodes.get(0);
					Real2Range boundingBox = CDXML2CMLProcessor.getNormalizedBoundingBox(arrow);
					if (boundingBox == null) {
						LOG.error("null boundingBox");
					} else {
						Nodes labelNodes = scopeElement.query("./cml:label", CML_XPATH);
						List<CMLLabel> labels = CDXML2CMLProcessor.getVerticalLabels(
								scopeElement, labelNodes, boundingBox, 50, -50,
								MIN_REACTION_LABEL_FONT_SIZE,
								MAX_REACTION_LABEL_FONT_SIZE
								);
						labels = CDXML2CMLProcessor.sortLabelsByY(labels);
						for (CMLLabel label : labels) {
							LOG.debug("L "+label.getCMLValue());
							// conditions?
							// DCM, 0{{169}}C, 0.5h 
							// FIXME
//							if (true) throw new RuntimeException("process commas");
//							addTextSplitAtCommas(label.getCMLValue(), reaction);
							label.addAttribute(new Attribute("convention", "cml:reaction-components"));
//							label.detach();
						}
					}
					arrow.detach();
				} else {
					LOG.error("Cannot find graphic arrow");
				}
			}
		} else {
			LOG.info("no reactionStepArrows");
		}
	}
	
	/**
	 * @param element
	 * @param reactantList
	 * @param reactantS
	 * @throws RuntimeException
	 */
	private void addMoleculeRefsToReactants(CMLReactantList reactantList) throws RuntimeException {
		String reactantS = reaction.getAttributeValue("ReactionStepReactants", CDX_NAMESPACE);
		if (reactantS == null) {
			throw new RuntimeException("Null reactant String");
		}
		String[] reactantIds = reactantS.split(S_SPACE);
		reactantList = new CMLReactantList();
		reaction.appendChild(reactantList);
		for (String id : reactantIds) {
			Nodes nodes = findNodesWithIds(scopeElement, id);
			if (nodes.size() == 0) {
				continue;
			}
			if (nodes.get(0) instanceof CMLMolecule) {
				CMLMolecule refMolecule = new CMLMolecule();
				refMolecule.setRef(id);
				CMLReactant reactant = new CMLReactant();
				reactantList.addReactant(reactant);
				reactant.addMolecule(refMolecule);
				addRoleAndLabel((CMLMolecule) nodes.get(0), refMolecule);
			} else if (nodes.get(0) instanceof CMLLabel) {
				LOG.error("Cannot use label as reactant: "+id);
			} else {
				throw new RuntimeException("unexpected reactant: "+id);
			}
		}
	}

	/**
	 * @param element
	 * @param id
	 * @return nodes
	 */
	private Nodes findNodesWithIds(CMLElement element, String id) {
		Nodes nodes = element.query("//*[@id='"+id+"']");
		if (nodes.size() ==0) {
			LOG.error("********Cannot find molecule or label: "+id);
		}
		return nodes;
	}

	/**
	 * @param nodes
	 * @param refMolecule
	 * @throws RuntimeException
	 */
	private void addRoleAndLabel(CMLMolecule molecule, CMLMolecule refMolecule) throws RuntimeException {
		if ("cdx:fragment".equals(molecule.getRole())) {
			refMolecule.setRole("reagent");
		}
		CMLElements<CMLLabel> labelList = molecule.getLabelElements();
		if (labelList.size() > 0) {
			CMLLabel label = new CMLLabel(labelList.get(0));
			label.removeAttribute("id");
			refMolecule.addLabel(label);
		}
	}
	
	/**
	 * @param element
	 * @param productList
	 * @param productS
	 * @throws RuntimeException
	 */
	private void addMoleculeRefsToProducts(CMLProductList productList) throws RuntimeException {
		String productS = reaction.getAttributeValue("ReactionStepProducts", CDX_NAMESPACE);
		if (productS == null) {
			throw new RuntimeException("Null product String");
		}
		String[] productIds = productS.split(S_SPACE);
		productList = new CMLProductList();
		reaction.appendChild(productList);
		for (String id : productIds) {
			Nodes nodes = findNodesWithIds(scopeElement, id);
			if (nodes.size() == 0) {
				continue;
			}
			if (nodes.get(0) instanceof CMLMolecule) {
				CMLMolecule refMolecule = new CMLMolecule();
				refMolecule.setRef(id);
				CMLProduct product = new CMLProduct();
				productList.addProduct(product);
				product.addMolecule(refMolecule);
				addRoleAndLabel((CMLMolecule) nodes.get(0), refMolecule);
			} else if (nodes.get(0) instanceof CMLLabel) {
				LOG.warn("Cannot use label as product: "+id);
			} else {
				throw new RuntimeException("unexpected product: "+id);
			}
		}
	}
	
	protected String getPrefix() {
		return null;
	}
}



