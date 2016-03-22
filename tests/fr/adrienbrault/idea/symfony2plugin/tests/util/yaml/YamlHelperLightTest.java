package fr.adrienbrault.idea.symfony2plugin.tests.util.yaml;


import fr.adrienbrault.idea.symfony2plugin.tests.SymfonyLightCodeInsightFixtureTestCase;
import fr.adrienbrault.idea.symfony2plugin.util.yaml.YamlHelper;
import fr.adrienbrault.idea.symfony2plugin.util.yaml.YamlPsiElementFactory;
import fr.adrienbrault.idea.symfony2plugin.util.yaml.visitor.YamlServiceTag;
import fr.adrienbrault.idea.symfony2plugin.util.yaml.visitor.YamlTagVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.YAMLFileType;
import org.jetbrains.yaml.psi.YAMLHash;
import org.jetbrains.yaml.psi.YAMLKeyValue;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class YamlHelperLightTest extends SymfonyLightCodeInsightFixtureTestCase {

    /**
     * @see fr.adrienbrault.idea.symfony2plugin.util.yaml.YamlHelper#visitTagsOnServiceDefinition
     */
    public void testVisitTagsOnServiceDefinition() {

        YAMLKeyValue yamlKeyValue = YamlPsiElementFactory.createFromText(getProject(), YAMLKeyValue.class, "foo:\n" +
            "    tags:\n" +
            "       - { name: kernel.event_listener, event: eventName, method: methodName }\n" +
            "       - { name: kernel.event_listener2, event: eventName2, method: methodName2 }\n"
        );

        ListYamlTagVisitor visitor = new ListYamlTagVisitor();
        YamlHelper.visitTagsOnServiceDefinition(yamlKeyValue, visitor);

        assertEquals("kernel.event_listener", visitor.getItem(0).getName());
        assertEquals("eventName", visitor.getItem(0).getAttribute("event"));
        assertEquals("methodName", visitor.getItem(0).getAttribute("method"));

        assertEquals("kernel.event_listener2", visitor.getItem(1).getName());
        assertEquals("eventName2", visitor.getItem(1).getAttribute("event"));
        assertEquals("methodName2", visitor.getItem(1).getAttribute("method"));
    }

    /**
     * @see fr.adrienbrault.idea.symfony2plugin.util.yaml.YamlHelper#visitTagsOnServiceDefinition
     */
    public void testVisitTagsOnServiceDefinitionWithQuote() {

        YAMLKeyValue yamlKeyValue = YamlPsiElementFactory.createFromText(getProject(), YAMLKeyValue.class, "foo:\n" +
            "    tags:\n" +
            "       - { name: 'kernel.event_listener', event: 'eventName', method: 'methodName' }\n"
        );

        ListYamlTagVisitor visitor = new ListYamlTagVisitor();
        YamlHelper.visitTagsOnServiceDefinition(yamlKeyValue, visitor);

        assertEquals("kernel.event_listener", visitor.getItem().getName());
        assertEquals("eventName", visitor.getItem().getAttribute("event"));
        assertEquals("methodName", visitor.getItem().getAttribute("method"));
    }

    /**
     * @see fr.adrienbrault.idea.symfony2plugin.util.yaml.YamlHelper#visitTagsOnServiceDefinition
     */
    public void testVisitTagsOnServiceDefinitionWithDoubleQuote() {

        YAMLKeyValue yamlKeyValue = YamlPsiElementFactory.createFromText(getProject(), YAMLKeyValue.class, "foo:\n" +
            "    tags:\n" +
            "       - { name: \"kernel.event_listener\", event: \"eventName\", method: \"methodName\" }\n"
        );

        ListYamlTagVisitor visitor = new ListYamlTagVisitor();
        YamlHelper.visitTagsOnServiceDefinition(yamlKeyValue, visitor);

        assertEquals("kernel.event_listener", visitor.getItem().getName());
        assertEquals("eventName", visitor.getItem().getAttribute("event"));
        assertEquals("methodName", visitor.getItem().getAttribute("method"));
    }

    /**
     * @see fr.adrienbrault.idea.symfony2plugin.util.yaml.YamlHelper#findServiceInContext
     */
    public void testFindServiceInContext() {
        assertEquals("foo", YamlHelper.findServiceInContext(myFixture.configureByText(YAMLFileType.YML, "" +
            "services:\n" +
            "  foo:\n" +
            "    tags:\n" +
            "      - { name: fo<caret>o}\n"
        ).findElementAt(myFixture.getCaretOffset())).getKeyText());

        assertEquals("foo", YamlHelper.findServiceInContext(myFixture.configureByText(YAMLFileType.YML, "" +
            "services:\n" +
            "  foo:\n" +
            "    class: fo<caret>o"
        ).findElementAt(myFixture.getCaretOffset())).getKeyText());
    }

    /**
     * @see fr.adrienbrault.idea.symfony2plugin.util.yaml.YamlHelper#getYamlKeyValueAsString
     */
    public void testGetYamlKeyValueAsString() {

        String[] strings = {
            "{ name: routing.loader, method: foo }",
            "{ name: routing.loader, method: 'foo' }",
            "{ name: routing.loader, method: \"foo\" }",
        };

        for (String s : strings) {
            assertEquals("foo", YamlHelper.getYamlKeyValueAsString(
                YamlPsiElementFactory.createFromText(getProject(), YAMLHash.class, s),
                "method"
            ));
        }
    }

    /**
     * @see fr.adrienbrault.idea.symfony2plugin.util.yaml.YamlHelper#collectServiceTags
     */
    public void testCollectServiceTags() {

        YAMLKeyValue fromText = YamlPsiElementFactory.createFromText(getProject(), YAMLKeyValue.class, "" +
            "foo:\n" +
            "  tags:\n" +
            "    - { name: routing.loader, method: crossHint }\n" +
            "    - { name: routing.loader1, method: crossHint }\n"
        );

        assertNotNull(fromText);
        assertContainsElements(YamlHelper.collectServiceTags(fromText), "routing.loader", "routing.loader1");
    }

    private static class ListYamlTagVisitor implements YamlTagVisitor {

        private List<YamlServiceTag> items = new ArrayList<YamlServiceTag>();

        @Override
        public void visit(@NotNull YamlServiceTag args) {
            items.add(args);
        }

        public YamlServiceTag getItem(int pos) {
            return items.get(pos);
        }

        public YamlServiceTag getItem() {
            return items.get(0);
        }
    }

}
