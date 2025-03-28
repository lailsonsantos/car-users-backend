
pipeline {
  agent any
  environment {
    HEROKU_API_KEY = credentials('heroku-api-key')
    HEROKU_APP_NAME = 'pitang-app-backend'
  }
  stages {
    stage('Build') {
      steps {
        sh 'mvn clean package -DskipTests'
      }
    }
    stage('Deploy to Heroku') {
      steps {
        sh '''
          curl https://cli-assets.heroku.com/install.sh | sh
          echo "$HEROKU_API_KEY" | heroku auth:token
          heroku git:remote -a $HEROKU_APP_NAME
          git push heroku HEAD:master --force
        '''
      }
    }
  }
}
