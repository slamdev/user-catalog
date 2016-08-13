package com.github.slamdev.catalog.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

class EC2Plugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.extensions.create(EC2Extension.NAME, EC2Extension)
        project.extensions.add(EC2Instances.NAME, new EC2Instances(project))
    }
}
