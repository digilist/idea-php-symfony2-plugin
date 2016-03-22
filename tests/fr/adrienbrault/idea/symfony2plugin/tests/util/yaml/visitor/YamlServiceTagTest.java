package fr.adrienbrault.idea.symfony2plugin.tests.util.yaml.visitor;

import fr.adrienbrault.idea.symfony2plugin.tests.SymfonyLightCodeInsightFixtureTestCase;
import fr.adrienbrault.idea.symfony2plugin.util.yaml.YamlPsiElementFactory;
import fr.adrienbrault.idea.symfony2plugin.util.yaml.visitor.YamlServiceTag;
import org.jetbrains.yaml.psi.YAMLHash;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 *
 * @see fr.adrienbrault.idea.symfony2plugin.util.yaml.visitor.YamlServiceTag
 */
public class YamlServiceTagTest extends SymfonyLightCodeInsightFixtureTestCase {
    public void testYmlTagAttributeExtraction() {
        YAMLHash fromText = YamlPsiElementFactory.createFromText(getProject(), YAMLHash.class, "{ name: routing.loader, method: foo }");
        YamlServiceTag tag = new YamlServiceTag("foo", fromText);

        assertEquals("foo", tag.getAttribute("method"));
        assertEquals("routing.loader", tag.getAttribute("name"));
    }
}
