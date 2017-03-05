# Julien
### Your personal Docker Assistant

Julien is your personal docker assistant made to help you with your Docker dev ops tasks.

Julien was built in 24 hours at Docker Hackathon at Holberton School.

Julien takes in differnet server env that's defined in the code and runs a script/command in it. Then the request is returned to Alexa that reads it back.

## Setup
### Define Envs
1) Setup Envs in `src/main/java/com/barry/julien/docker/Enviornment.java` file.
2) run the command `gradle shadowJar` in the root folder

### AWS Lambda Setup
1. Go to the AWS Console and click on the Lambda link. Note: ensure you are in us-east or you wont be able to use Alexa with Lambda.
2. Click on the Create a Lambda Function or Get Started Now button.
3. Skip the blueprint
4. Configure Triggers Screen click the outlined empty square and select Alexa Skill Kit.  Click Next
5. Name the Lambda Function "Julien-Skill".
6. Select the runtime as Java 8
7. Select Code entry type as "Upload a .ZIP file" and then upload the `julien-0.1-all.jar` file in the `build/libs` directory to Lambda
8. Set the Handler as JulienSpeechletStreamHandler (this refers to the Lambda RequestStreamHandler file in the zip).
9. Create a basic execution role and click create.
10. Leave the Advanced settings as the defaults.
11. Click "Next" and review the settings then click "Create Function"
12. Copy the ARN from the top right to be used later in the Alexa Skill Setup.

### Alexa Skill Setup
1. Go to the [Alexa Console](https://developer.amazon.com/edw/home.html) and click Add a New Skill.
2. Set "Julien" as the skill name and "julien" as the invocation name, this is what is used to activate your skill. For example you would say: "Alexa, tell julien to say list containers on development."
3. Select the Lambda ARN for the skill Endpoint and paste the ARN copied from above. Click Next.
4. Copy the Intent Schema from the included `docs/IntentSchema.json`.
5. Copy the Sample Utterances from the included `docs/SampleUtterances.txt`. Click Next.
6. Create two custom slot named according to the two two slots in the `docs/customSlots` folder.
7. Copy the contents of the two .txt files into the custom slots.
8. Go back to the skill Information tab and copy the appId. Paste the appId into the `src/main/java/com/barry/julien/handler/ask/JulienSpeechletStreamHandler.java` file for the variable supportedApplicationIds,
   then update the lambda source zip file with this change and upload to lambda again, this step makes sure the lambda function only serves request from authorized source.
9. You are now able to start testing your sample skill! You should be able to go to the [Echo webpage](http://echo.amazon.com/#skills) and see your skill enabled.
10. In order to test it, try to say some of the Sample Utterances from the Examples section below.
11. Your skill is now saved and once you are finished testing you can continue to publish your skill.

## Examples
### One-shot model:
    User: "Alexa, ask julien to list containers on development."
    Alexa: "Here's a list of containers on the development enviornment:...."

## Contributers
[@glyif](https://github.com/glyif)

[@Hillmonkey](https://github.com/Hillmonkey)

