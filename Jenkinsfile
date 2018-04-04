pipeline {
    agent any

    triggers {
        pollSCM('* * * * *')
    }

    stages {
        stage('build') {
            agent {
                docker {
                    image 'maven:latest'
                }

            }
            steps {
                sh 'mvn --settings .travis.settings.xml clean install -Ddockerfile.skip'
            }
        }
        stage('snapshot') {
            agent {
                docker {
                    image 'maven:latest'
                }

            }
            environment {
                ARTIFACTORY = credentials('ARTIFACTORY')
            }
            steps {
                sh 'mvn --settings .travis.settings.xml deploy -Ddockerfile.skip'
            }
        }

        stage('dev') {
            agent {
                docker {
                    image 'governmentpaas/cf-cli'
                    args '-u root'
                }

            }

            environment {
                CLOUDFOUNDRY_API = credentials('CLOUDFOUNDRY_API')
                CF_DOMAIN = credentials('CF_DOMAIN')
                DEV_SECURITY = credentials('DEV_SECURITY')
                CF_USER = credentials('CF_USER')

                NOTIFY_TESTAPIKEY = credentials('NOTIFY_TESTAPIKEY')
                NOTIFY_CENSUSUACSMSTEMPLATEID = credentials('NOTIFY_CENSUSUACSMSTEMPLATEID')
                NOTIFY_ONSSURVEYSRASEMAILREMINDERTEMPLATEID = credentials('NOTIFY_ONSSURVEYSRASEMAILREMINDERTEMPLATEID')
            }
            steps {
                sh "find . -type f -name '*notifygatewaysvc*.jar' -not -name '*docker-info*' -exec mv {} target/notifygatewaysvc.jar \\;"
                sh "sed -i -- 's/SPACE/dev/g' *template.yml"
                sh "sed -i -- 's/INSTANCES/1/g' *template.yml"
                sh "sed -i -- 's/DATABASE/rm-pg-db/g' *template.yml"
                sh "sed -i -- 's/ENDPOINT_ENABLED/'false'/g' *template.yml"
                sh "sed -i -- 's/true/'true'/g' *template.yml"
                sh "sed -i -- 's/REPLACE_BA_USERNAME/${env.DEV_SECURITY_USR}/g' *template.yml"
                sh "sed -i -- 's/REPLACE_BA_PASSWORD/${env.DEV_SECURITY_PSW}/g' *template.yml"

                sh "echo '    notify_apiKey: ${env.NOTIFY_TESTAPIKEY}' >> manifest-template.yml"
                sh "echo '    notify_censusUacSmsTemplateId: ${env.NOTIFY_CENSUSUACSMSTEMPLATEID}' >> manifest-template.yml"
                sh "echo '    notify_onsSurveysRasEmailReminderTemplateId: ${env.NOTIFY_ONSSURVEYSRASEMAILREMINDERTEMPLATEID}' >> manifest-template.yml"
                sh "echo '    notify_enabled: \"true\"' >> manifest-template.yml"

                sh "cf login -a https://${env.CLOUDFOUNDRY_API} --skip-ssl-validation -u ${CF_USER_USR} -p ${CF_USER_PSW} -o rmras -s dev"
                sh 'cf push -f manifest-template.yml'
                sh 'git reset --hard'
            }
        }


        stage('ci?') {
            agent none
            steps {
                script {
                    try {
                        timeout(time: 60, unit: 'SECONDS') {
                            script {
                                env.deploy_ci = input message: 'Deploy to CI?', id: 'deploy_ci', parameters: [choice(name: 'Deploy to CI', choices: 'no\nyes', description: 'Choose "yes" if you want to deploy to CI')]
                            }
                        }
                    } catch (ignored) {
                        echo 'Skipping ci deployment'
                    }
                }
            }
        }

        stage('ci') {
            agent {
                docker {
                    image 'governmentpaas/cf-cli'
                    args '-u root'
                }

            }
            when {
                environment name: 'deploy_ci', value: 'yes'
            }

            environment {
                CLOUDFOUNDRY_API = credentials('CLOUDFOUNDRY_API')
                CF_DOMAIN = credentials('CF_DOMAIN')
                CI_SECURITY = credentials('CI_SECURITY')
                CF_USER = credentials('CF_USER')

                NOTIFY_TESTAPIKEY = credentials('NOTIFY_TESTAPIKEY')
                NOTIFY_CENSUSUACSMSTEMPLATEID = credentials('NOTIFY_CENSUSUACSMSTEMPLATEID')
                NOTIFY_ONSSURVEYSRASEMAILREMINDERTEMPLATEID = credentials('NOTIFY_ONSSURVEYSRASEMAILREMINDERTEMPLATEID')
            }
            steps {
                sh "find . -type f -name '*notifygatewaysvc*.jar' -not -name '*docker-info*' -exec mv {} target/notifygatewaysvc.jar \\;"
                sh "sed -i -- 's/SPACE/ci/g' *template.yml"
                sh "sed -i -- 's/INSTANCES/1/g' *template.yml"
                sh "sed -i -- 's/DATABASE/rm-pg-db/g' *template.yml"
                sh "sed -i -- 's/ENDPOINT_ENABLED/'false'/g' *template.yml"
                sh "sed -i -- 's/true/'true'/g' *template.yml"
                sh "sed -i -- 's/REPLACE_BA_USERNAME/${env.CI_SECURITY_USR}/g' *template.yml"
                sh "sed -i -- 's/REPLACE_BA_PASSWORD/${env.CI_SECURITY_PSW}/g' *template.yml"

                sh "echo '    notify_apiKey: ${env.NOTIFY_TESTAPIKEY}' >> manifest-template.yml"
                sh "echo '    notify_censusUacSmsTemplateId: ${env.NOTIFY_CENSUSUACSMSTEMPLATEID}' >> manifest-template.yml"
                sh "echo '    notify_onsSurveysRasEmailReminderTemplateId: ${env.NOTIFY_ONSSURVEYSRASEMAILREMINDERTEMPLATEID}' >> manifest-template.yml"
                sh "echo '    notify_enabled: \"true\"' >> manifest-template.yml"

                sh "cf login -a https://${env.CLOUDFOUNDRY_API} --skip-ssl-validation -u ${CF_USER_USR} -p ${CF_USER_PSW} -o rmras -s ci"
                sh 'cf push -f manifest-template.yml'
                sh 'git reset --hard'
            }
        }

        stage('release?') {
            agent none
            steps {
                script {
                    try {
                        timeout(time: 60, unit: 'SECONDS') {
                            script {
                                env.do_release = input message: 'Do a release?', id: 'do_release', parameters: [choice(name: 'Release', choices: 'no\nyes', description: 'Choose "yes" if you want to create a release artifact and tag')]
                            }
                        }
                    } catch (ignored) {
                        echo 'Skipping release'
                    }
                }
            }
        }

        stage('release') {
            agent {
                docker {
                    image 'maven:latest'
                }

            }
            environment {
                ARTIFACTORY = credentials('ARTIFACTORY')
                GITHUB_API_KEY = credentials('GITHUB_API_KEY')
            }
            when {
                environment name: 'do_release', value: 'yes'
            }
            steps {
                sh 'git tag -d $(git tag -l)'
                sh 'git config --local user.email "jenkins@jenkins2.rmdev.onsdigital.uk"'
                sh 'git config --local user.name "Jenkins";'
                sh "mvn --settings .travis.settings.xml -B clean release:clean release:prepare -Dusername=${GITHUB_API_KEY} -Darguments='-Ddockerfile.skip'"
                sh "mvn --settings .travis.settings.xml -B release:perform -Dusername=${GITHUB_API_KEY} -Darguments='-Dmaven.javadoc.skip=true -Ddockerfile.skip'"
            }
        }

        stage('test') {
            agent {
                docker {
                    image 'governmentpaas/cf-cli'
                    args '-u root'
                }

            }
            when {
                environment name: 'do_release', value: 'yes'
            }

            environment {
                CLOUDFOUNDRY_API = credentials('CLOUDFOUNDRY_API')
                CF_DOMAIN = credentials('CF_DOMAIN')
                TEST_SECURITY = credentials('TEST_SECURITY')
                CF_USER = credentials('CF_USER')

                NOTIFY_TESTAPIKEY = credentials('NOTIFY_TESTAPIKEY')
                NOTIFY_CENSUSUACSMSTEMPLATEID = credentials('NOTIFY_CENSUSUACSMSTEMPLATEID')
                NOTIFY_ONSSURVEYSRASEMAILREMINDERTEMPLATEID = credentials('NOTIFY_ONSSURVEYSRASEMAILREMINDERTEMPLATEID')
            }
            steps {
                sh 'git reset --hard'
                sh 'rm -r target && mkdir target'
                sh 'wget https://gist.githubusercontent.com/benjefferies/106d53e3178e1627bcad4784f6fe7fe1/raw/832c07c0f3e31933e634a9e0a2398d2845943090/artifactory-get.sh'
                sh 'sh artifactory-get.sh -r http://artifactory-sdc.onsdigital.uk/artifactory/libs-release-local/ -g uk.gov.ons.ctp.product -a notifygatewaysvc > target/notifygatewaysvc.jar'
                sh 'chmod 777 target/notifygatewaysvc.jar'
                sh "sed -i -- 's/SPACE/test/g' *template.yml"
                sh "sed -i -- 's/INSTANCES/1/g' *template.yml"
                sh "sed -i -- 's/DATABASE/rm-pg-db/g' *template.yml"
                sh "sed -i -- 's/ENDPOINT_ENABLED/'false'/g' *template.yml"
                sh "sed -i -- 's/true/'true'/g' *template.yml"
                sh "sed -i -- 's/REPLACE_BA_USERNAME/${env.TEST_SECURITY_USR}/g' *template.yml"
                sh "sed -i -- 's/REPLACE_BA_PASSWORD/${env.TEST_SECURITY_PSW}/g' *template.yml"
                sh "echo '    notify_apiKey: ${env.NOTIFY_TESTAPIKEY}' >> manifest-template.yml" // SIT will use NOTIFY_TESTAPIKEY
                sh "echo '    notify_censusUacSmsTemplateId: ${env.NOTIFY_CENSUSUACSMSTEMPLATEID}' >> manifest-template.yml"
                sh "echo '    notify_onsSurveysRasEmailReminderTemplateId: ${env.NOTIFY_ONSSURVEYSRASEMAILREMINDERTEMPLATEID}' >> manifest-template.yml"
                sh "echo '    notify_enabled: \"true\"' >> manifest-template.yml"

                sh "cf login -a https://${env.CLOUDFOUNDRY_API} --skip-ssl-validation -u ${CF_USER_USR} -p ${CF_USER_PSW} -o rmras -s test"
                sh 'cf push -f manifest-template.yml'
                sh 'rm artifactory-get.sh'
                sh 'rm target/notifygatewaysvc.jar'
            }
        }
    }

    post {
        always {
            cleanWs()
            dir('${env.WORKSPACE}@tmp') {
                deleteDir()
            }
            dir('${env.WORKSPACE}@script') {
                deleteDir()
            }
            dir('${env.WORKSPACE}@script@tmp') {
                deleteDir()
            }
        }
    }
}