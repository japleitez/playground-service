package eu.europa.ec.eurostat.wihp;

import static com.tngtech.archunit.base.DescribedPredicate.alwaysTrue;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.belongToAnyOf;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

import com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import eu.europa.ec.eurostat.wihp.exceptions.BadRequestAlertException;
import eu.europa.ec.eurostat.wihp.exceptions.NotFoundAlertException;
import eu.europa.ec.eurostat.wihp.exceptions.UnprocessableEntityException;

@AnalyzeClasses(packagesOf = PlaygroundServiceApp.class, importOptions = DoNotIncludeTests.class)
class TechnicalStructureTest {

    // prettier-ignore
    @ArchTest
    static final ArchRule respectsTechnicalArchitectureLayers = layeredArchitecture()
        .layer("Config").definedBy("..config..")
        .layer("Client").definedBy("..client..")
        .layer("Web").definedBy("..web..")
        .optionalLayer("Service").definedBy("..service..")
        .layer("Security").definedBy("..security..")

        .whereLayer("Config").mayNotBeAccessedByAnyLayer()
        .whereLayer("Client").mayNotBeAccessedByAnyLayer()
        .whereLayer("Web").mayOnlyBeAccessedByLayers("Config")
        .whereLayer("Service").mayOnlyBeAccessedByLayers("Web", "Config")
        .whereLayer("Security").mayOnlyBeAccessedByLayers("Config", "Client", "Service", "Web")

        .ignoreDependency(belongToAnyOf(PlaygroundServiceApp.class, BadRequestAlertException.class,
            NotFoundAlertException.class,
            UnprocessableEntityException.class), alwaysTrue())
        .ignoreDependency(alwaysTrue(), belongToAnyOf(
            eu.europa.ec.eurostat.wihp.config.ApplicationProperties.class
        ));
}
