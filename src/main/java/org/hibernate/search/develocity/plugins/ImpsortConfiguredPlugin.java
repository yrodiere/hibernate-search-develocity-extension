package org.hibernate.search.develocity.plugins;

import java.util.Map;

import org.hibernate.search.develocity.SimpleConfiguredPlugin;

import com.gradle.maven.extension.api.cache.MojoMetadataProvider;
import com.gradle.maven.extension.api.cache.MojoMetadataProvider.Context.FileSet.EmptyDirectoryHandling;
import com.gradle.maven.extension.api.cache.MojoMetadataProvider.Context.FileSet.NormalizationStrategy;

public class ImpsortConfiguredPlugin extends SimpleConfiguredPlugin {

    @Override
    protected String getPluginName() {
        return "impsort-maven-plugin";
    }

    @Override
    protected Map<String, GoalMetadataProvider> getGoalMetadataProviders() {
        return Map.of(
                "sort", ImpsortConfiguredPlugin::configureSort);
    }

    private static void configureSort(MojoMetadataProvider.Context context) {
        context.inputs(inputs -> {
            dependsOnGav(inputs, context);

            inputs.properties("sourceEncoding", "skip", "staticGroups", "groups", "staticAfter", "joinStaticWithNonStatic",
                    "includes", "excludes", "removeUnused", "treatSamePackageAsUnused", "breadthFirstComparator",
                    "lineEnding", "compliance");

            inputs.fileSet("sourceDirectory", fileSet -> fileSet.normalizationStrategy(NormalizationStrategy.RELATIVE_PATH)
                    .emptyDirectoryHandling(EmptyDirectoryHandling.IGNORE));
            inputs.fileSet("testSourceDirectory", fileSet -> fileSet.normalizationStrategy(NormalizationStrategy.RELATIVE_PATH)
                    .emptyDirectoryHandling(EmptyDirectoryHandling.IGNORE));
            inputs.fileSet("directories", fileSet -> fileSet.normalizationStrategy(NormalizationStrategy.RELATIVE_PATH)
                    .emptyDirectoryHandling(EmptyDirectoryHandling.IGNORE));

            inputs.ignore("project", "plugin",
                    // For now, we need to ignore the cachedir until we can declare it as an output. See below.
                    "cachedir");
        });

        context.outputs(outputs -> {
            outputs.cacheable("If the inputs and dependencies are identical, we should have the same output");

            // For now we don't want to output the cachedir as it contains absolute paths
            // See https://github.com/revelc/impsort-maven-plugin/pull/87
            //outputs.directory("cachedir");
        });
    }
}
