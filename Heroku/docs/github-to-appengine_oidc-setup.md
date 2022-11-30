# Setup OpenID Connect (OIDC) for GitHub to App Engine

## Missing auth
https://github.com/TWiStErRob/net.twisterrob.cinema/actions/runs/3534670250/jobs/5931815183

Problem from `gradlew appengineDeploy`
> ERROR: (gcloud.app.deploy) You do not currently have an active account selected.
Please run:

### Details
```
$ gcloud auth login

to obtain new credentials.

If you have already logged in with a different account:

    $ gcloud config set account ACCOUNT

to select an already authenticated account to use.
```

### Solution
 * https://github.com/google-github-actions/auth#setup
 * https://cloud.google.com/blog/products/identity-security/enabling-keyless-authentication-from-github-actions

```shell
gcloud iam workload-identity-pools create "cicd" --project="twisterrob-cinema" --location="global" --display-name="CI/CD Pool"
gcloud iam workload-identity-pools providers create-oidc "github-actions" --project="twisterrob-cinema" --location="global" --workload-identity-pool="cicd" --display-name="GitHub Actions Provider" --attribute-mapping="google.subject=assertion.sub,attribute.actor=assertion.actor,attribute.repository=assertion.repository,attribute.aud=assertion.aud" --issuer-uri="https://token.actions.githubusercontent.com"
gcloud iam workload-identity-pools providers describe "github-actions" --project="twisterrob-cinema" --location="global" --workload-identity-pool="cicd" --format="value(name)"
# OUTPUT: projects/8667575547/locations/global/workloadIdentityPools/cicd/providers/github-actions
# But use:projects/8667575547/locations/global/workloadIdentityPools/cicd/attribute.repository/TWiStErRob/net.twisterrob.cinema

# SET UP: github-actions@twisterrob-cinema.iam.gserviceaccount.com  
gcloud iam service-accounts create "github-actions" --project="twisterrob-cinema" --description="Deploy to Google App Engine from GitHub Actions" --display-name="GitHub Actions CI/CD"
# Effective permissions of service account:
# https://console.cloud.google.com/iam-admin/analyzer/query;identity=serviceAccount:github-actions@twisterrob-cinema.iam.gserviceaccount.com;expand=roles;scopeResource=twisterrob-cinema;scopeResourceType=0;templateId=ACCESS_TERMINATED_EMPLOYEE/report?project=twisterrob-cinema

# All the roles: https://cloud.google.com/iam/docs/understanding-roles
# Resource names: https://cloud.google.com/iam/docs/full-resource-names
gcloud iam list-grantable-roles --project="twisterrob-cinema" "//iam.googleapis.com/projects/twisterrob-cinema/serviceAccounts/github-actions@twisterrob-cinema.iam.gserviceaccount.com"

# description: Impersonate service accounts from GKE Workloads
# name: roles/iam.workloadIdentityUser
# title: Workload Identity User
# https://cloud.google.com/iam/docs/understanding-roles#iam.workloadIdentityUser
gcloud iam service-accounts add-iam-policy-binding "github-actions@twisterrob-cinema.iam.gserviceaccount.com" --project="twisterrob-cinema" --role="roles/iam.workloadIdentityUser" --member="principalSet://iam.googleapis.com/projects/8667575547/locations/global/workloadIdentityPools/cicd/attribute.repository/TWiStErRob/net.twisterrob.cinema"

# description: Give App Engine Standard Enviroment service account access to managed resources. Includes access to service accounts.
# name: roles/appengine.serviceAgent
# title: App Engine Standard Environment Service Agent
# https://cloud.google.com/iam/docs/understanding-roles#appengine.serviceAgent
gcloud iam service-accounts add-iam-policy-binding "github-actions@twisterrob-cinema.iam.gserviceaccount.com" --project="twisterrob-cinema" --role="roles/appengine.serviceAgent" --member="principalSet://iam.googleapis.com/projects/8667575547/locations/global/workloadIdentityPools/cicd/attribute.repository/TWiStErRob/net.twisterrob.cinema"
# ERROR: (gcloud.iam.service-accounts.add-iam-policy-binding) INVALID_ARGUMENT: Only service accounts can be granted ServiceAgent roles.

gcloud projects add-iam-policy-binding twisterrob-cinema --member="serviceAccount:github-actions@twisterrob-cinema.iam.gserviceaccount.com" --role="roles/appengine.deployer"
gcloud projects add-iam-policy-binding twisterrob-cinema --member="serviceAccount:github-actions@twisterrob-cinema.iam.gserviceaccount.com" --role="roles/storage.objectAdmin"
gcloud projects add-iam-policy-binding twisterrob-cinema --member="serviceAccount:github-actions@twisterrob-cinema.iam.gserviceaccount.com" --role="roles/iam.serviceAccountUser"
gcloud projects add-iam-policy-binding twisterrob-cinema --member="serviceAccount:github-actions@twisterrob-cinema.iam.gserviceaccount.com" --role="roles/cloudbuild.builds.editor"
gcloud projects add-iam-policy-binding twisterrob-cinema --member="serviceAccount:github-actions@twisterrob-cinema.iam.gserviceaccount.com" --role="roles/appengine.serviceAdmin"
#gcloud projects remove-iam-policy-binding twisterrob-cinema --member="serviceAccount:github-actions@twisterrob-cinema.iam.gserviceaccount.com" --role="roles/..."
```

### Confirmation
Different error message, token is invalid, instead of missing.

## Missing `ACTIONS_ID_TOKEN_REQUEST_TOKEN`
https://github.com/TWiStErRob/net.twisterrob.cinema/actions/runs/3536067122

Problem from `google-github-actions/auth`

> Error: google-github-actions/auth failed with: retry function failed after 1 attempt: gitHub Actions did not inject $ACTIONS_ID_TOKEN_REQUEST_TOKEN or $ACTIONS_ID_TOKEN_REQUEST_URL into this job. This most likely means the GitHub Actions workflow permissions are incorrect, or this job is being run from a fork. For more information, please see https://docs.github.com/en/actions/security-guides/automatic-token-authentication#permissions-for-the-github_token

### Solution
https://github.com/google-github-actions/auth#configuring-gcloud, but with lower permission.
```yaml
jobs:
  release:
    permissions:
      contents: read
      id-token: read
```


## Invalid permission `id-token: read`
https://github.com/TWiStErRob/net.twisterrob.cinema/actions/runs/3536103159

Invalid workflow file: `.github/workflows/Release to Google App Engine.yml`
> The workflow is not valid. .github/workflows/Release to Google App Engine.yml (Line: 18, Col: 17): Unexpected value 'read'

### Solution
https://github.com/google-github-actions/auth#configuring-gcloud
```yaml
jobs:
  release:
    permissions:
      contents: read
      id-token: write
```

### Confirmation
`google-github-actions/auth` is executed.


## Invalid `audience`
https://github.com/TWiStErRob/net.twisterrob.cinema/actions/runs/3536133425/jobs/5934890160

Problem from `gradlew appengineDeploy` after first trying to use
> INFO: Using alternate credentials from file: [gha-creds-....json]

> ERROR: (gcloud.app.deploy) There was a problem refreshing your current auth tokens: Error code invalid_request: Invalid value for "audience". This value should be the full resource name of the Identity Provider. See https://cloud.google.com/iam/docs/reference/sts/rest/v1/TopLevel/token for the list of possible formats.

### Details
```json
{
	"error": "invalid_request",
	"error_description": "Invalid value for \"audience\". This value should be the full resource name of the Identity Provider. See https://cloud.google.com/iam/docs/reference/sts/rest/v1/TopLevel/token for the list of possible formats."
}
```

### Solution
https://github.com/google-github-actions/deploy-appengine/issues/247#issuecomment-1326181666
Fix `workload_identity_provider` value to be the output of
```shell
gcloud iam workload-identity-pools providers describe "github-actions" --project="twisterrob-cinema" --location="global" --workload-identity-pool="cicd" --format="value(name)"
```

### Confirmation
Different error message.


## Missing Google Cloud API
https://github.com/TWiStErRob/net.twisterrob.cinema/actions/runs/3539268623/jobs/5940974715

Problem from `gradlew appengineDeploy` after first trying to use
> INFO: Using alternate credentials from file: [gha-creds-....json]

> ERROR: (gcloud.app.deploy) There was a problem refreshing your current auth tokens: Unable to acquire impersonated credentials

### Details
```json
{
	"error": {
		"code": 403,
		"message": "IAM Service Account Credentials API has not been used in project 8667575547 before or it is disabled. Enable it by visiting https://console.developers.google.com/apis/api/iamcredentials.googleapis.com/overview?project=8667575547 then retry. If you enabled this API recently, wait a few minutes for the action to propagate to our systems and retry.",
		"status": "PERMISSION_DENIED",
		"details": [
			{
				"@type": "type.googleapis.com/google.rpc.Help",
				"links": [
					{
						"description": "Google developers console API activation",
						"url": "https://console.developers.google.com/apis/api/iamcredentials.googleapis.com/overview?project=8667575547"
					}
				]
			},
			{
				"@type": "type.googleapis.com/google.rpc.ErrorInfo",
				"reason": "SERVICE_DISABLED",
				"domain": "googleapis.com",
				"metadata": {
					"consumer": "projects/8667575547",
					"service": "iamcredentials.googleapis.com"
				}
			}
		]
	}
}
```

### Solution
Enable "IAM Service Account Credentials API" at https://console.cloud.google.com/apis/library/iamcredentials.googleapis.com

### Confirmation
Different error message.


## Missing Google Cloud API
https://github.com/TWiStErRob/net.twisterrob.cinema/actions/runs/3539365223

Problem from `gradlew appengineDeploy` fails after
> INFO: Reading [<googlecloudsdk.api_lib.storage.storage_util.ObjectReference object at 0x7f4b2e85a910>]

> ERROR: (gcloud.app.deploy) User [None] does not have permission to access apps instance [twisterrob-cinema] (or it may not exist): App Engine Admin API has not been used in project 8667575547 before or it is disabled. Enable it by visiting https://console.developers.google.com/apis/api/appengine.googleapis.com/overview?project=8667575547 then retry. If you enabled this API recently, wait a few minutes for the action to propagate to our systems and retry.

### Details
```yaml
- '@type': type.googleapis.com/google.rpc.Help
  links:
  - description: Google developers console API activation
    url: https://console.developers.google.com/apis/api/appengine.googleapis.com/overview?project=8667575547
- '@type': type.googleapis.com/google.rpc.ErrorInfo
  domain: googleapis.com
  metadata:
    consumer: projects/8667575547
    service: appengine.googleapis.com
  reason: SERVICE_DISABLED
```

### Solution
Enable "App Engine Admin API" at https://console.cloud.google.com/apis/library/appengine.googleapis.com

### Confirmation
Different error message.

## Missing permission: appengine.deployer
https://github.com/TWiStErRob/net.twisterrob.cinema/actions/runs/3539446204/jobs/5941350821
https://github.com/TWiStErRob/net.twisterrob.cinema/actions/runs/3539545180/jobs/5942255936

Problem from `gradlew appengineDeploy` fails after
> INFO: Reading [<googlecloudsdk.api_lib.storage.storage_util.ObjectReference object at 0xhexhexhexhex>]

> ERROR: (gcloud.app.deploy) Permissions error fetching application [apps/twisterrob-cinema]. Please make sure that you have permission to view applications on the project and that None has the App Engine Deployer (roles/appengine.deployer) role.

### Solution
[Grant recommended role `roles/appengine.deployer`](https://cloud.google.com/appengine/docs/legacy/standard/python/roles#recommended_role_for_application_deployment)
via
[Optional step 2](https://cloud.google.com/iam/docs/creating-managing-service-accounts#creating).
```shell
gcloud projects add-iam-policy-binding twisterrob-cinema --member="serviceAccount:github-actions@twisterrob-cinema.iam.gserviceaccount.com" --role="roles/appengine.deployer"
```

### Confirmation
Next step is started.
```
Services to deploy:

descriptor:                  [/home/runner/work/net.twisterrob.cinema/net.twisterrob.cinema/Heroku/deploy/appengine/build/staged-app/app.yaml]
source:                      [/home/runner/work/net.twisterrob.cinema/net.twisterrob.cinema/Heroku/deploy/appengine/build/staged-app]
target project:              [twisterrob-cinema]
target service:              [default]
target version:              [2]
target url:                  [https://twisterrob-cinema.appspot.com]
target service account:      [App Engine default service account]


Beginning deployment of service [default]...
```


## Missing permission: storage.objects.list
https://github.com/TWiStErRob/net.twisterrob.cinema/actions/runs/3539545180/jobs/5941571466

Problem from `gradlew appengineDeploy` fails after
> INFO: Using ignore file at [deploy/appengine/build/staged-app/.gcloudignore].

> ERROR: (gcloud.app.deploy) 403 Could not list bucket [staging.twisterrob-cinema.appspot.com]: *** does not have storage.objects.list access to the Google Cloud Storage bucket. Permission 'storage.objects.list' denied on resource (or it may not exist).

### Solution
[Grant recommended role `roles/storage.objectAdmin`](https://cloud.google.com/appengine/docs/legacy/standard/python/roles#recommended_role_for_application_deployment)
via
[Optional step 2](https://cloud.google.com/iam/docs/creating-managing-service-accounts#creating).
```shell
gcloud projects add-iam-policy-binding twisterrob-cinema --member="serviceAccount:github-actions@twisterrob-cinema.iam.gserviceaccount.com" --role="roles/storage.objectAdmin"
```

### Confirmation
It was trying to determine list of files already uploaded, so that it can do:
> INFO: Incremental upload skipped 100.0% of data
> INFO: Uploading [...] to [staging.twisterrob-cinema.appspot.com/...]


## Missing permission: act as owner
https://github.com/TWiStErRob/net.twisterrob.cinema/actions/runs/3539545180/jobs/5942896269
https://github.com/TWiStErRob/net.twisterrob.cinema/actions/runs/3539545180/jobs/5942346790

Problem from `gradlew appengineDeploy` fails after
> File upload done.  
> INFO: Manifest: [{ ... }]

> ERROR: (gcloud.app.deploy) PERMISSION_DENIED: You do not have permission to act as 'twisterrob-cinema@appspot.gserviceaccount.com'

### Details
```yaml
- '@type': type.googleapis.com/google.rpc.ResourceInfo
  description: You do not have permission to act as this service account.
  resourceName: twisterrob-cinema@appspot.gserviceaccount.com
  resourceType: serviceAccount
```

### Solution
[Grant recommended role `roles/iam.serviceAccountUser`](https://cloud.google.com/appengine/docs/legacy/standard/python/roles#recommended_role_for_application_deployment)
via
[Optional step 2](https://cloud.google.com/iam/docs/creating-managing-service-accounts#creating).
```shell
gcloud projects add-iam-policy-binding twisterrob-cinema --member="serviceAccount:github-actions@twisterrob-cinema.iam.gserviceaccount.com" --role="roles/iam.serviceAccountUser"
```

### Confirmation
Next step is started:
```
Updating service [default]...
```

## Missing permission: CreateBuild
https://github.com/TWiStErRob/net.twisterrob.cinema/actions/runs/3539545180/jobs/5942947234

Problem from `gradlew appengineDeploy` fails after
> Updating service [default]...

> .....failed.

### Details
> ERROR: (gcloud.app.deploy) Error Response: [7]
> Failed to create cloud build:
> IAM authority does not have the permission 'cloudbuild.builds.create' required for action CreateBuild on resource 'projects/twisterrob-cinema'.
> Explanation:
> Security Context:
```
RecordingSecurityContext{
    delegate=ValidatedSecurityContextWithSystemAuthorizationPolicy{
        delegate=ValidatedSecurityContextWithRegistryHandle{
            delegate=ValidatedSecurityContextWithObligations{
                delegate=ContextWithGaiaMintToken{
                    delegate=ValidatedIamSecurityContext{
                        user=gaiauser/0xa09cddd3c5,
                        creds=EndUserCreds{
                            loggable_credential {
                                type: GAIA_MINT loggable_gaia_mint { }
                            }
                            loggable_credential {
                                type: SERVICE_CONTROL_TOKEN
                            }
                        },
                        peer=protocol=loas;
                            psp_version=0;
                            level=strong_privacy_and_integrity;
                            host=inac82.prod.google.com;
                            is_authenticated_host=false;
                            role=cloud-build-api;
                            user=app-engine-zeus-worker;
                            is_delegated=true,
                        InternalIAMIdentity{
                            log=data_access_reason {
                                manual_reason: "Acquiring DAT for long-lived Admin API procedures."
                            }
                            originator {
                                scope: GAIA_USER gaia_user {
                                    user_id: 689826550725
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
```
> com.google.apps.framework.auth.IamPermissionDeniedException: ...

### Solution
[Grant recommended role `roles/cloudbuild.builds.editor`](https://cloud.google.com/appengine/docs/legacy/standard/python/roles#recommended_role_for_application_deployment)
via
[Optional step 2](https://cloud.google.com/iam/docs/creating-managing-service-accounts#creating).
```shell
gcloud projects add-iam-policy-binding twisterrob-cinema --member="serviceAccount:github-actions@twisterrob-cinema.iam.gserviceaccount.com" --role="roles/cloudbuild.builds.editor"
```

### Confirmation
Updating service [default]...
..........done.


## Missing permission: changing traffic split
https://github.com/TWiStErRob/net.twisterrob.cinema/actions/runs/3539545180/jobs/5943014441

Problem from `gradlew appengineDeploy` fails after
> Updating service [default]...
> ..........done.

> ERROR: (gcloud.app.deploy) Your deployment has succeeded, but promoting the new version to default failed. You may not have permissions to change traffic splits. Changing traffic splits requires the Owner, Editor, App Engine Admin, or App Engine Service Admin role. Please contact your project owner and use the `gcloud app services set-traffic --splits <version>=1` command to redirect traffic to your newly deployed version.

### Solution
Based on
[IAM Permissions granted to service account](https://console.cloud.google.com/iam-admin/analyzer/query;identity=serviceAccount:github-actions@twisterrob-cinema.iam.gserviceaccount.com;expand=roles;scopeResource=twisterrob-cinema;scopeResourceType=0;templateId=ACCESS_TERMINATED_EMPLOYEE/report?project=twisterrob-cinema)
and the
[Permissions contained within "App Engine Service Admin" role](https://cloud.google.com/iam/docs/understanding-roles#appengine.serviceAdmin)
and the
[Permissions contained within "App Engine Admin" role](https://cloud.google.com/iam/docs/understanding-roles#appengine.appAdmin)
I think the missing permission is `appengine.services.update`.

```shell
gcloud projects add-iam-policy-binding twisterrob-cinema --member="serviceAccount:github-actions@twisterrob-cinema.iam.gserviceaccount.com" --role="roles/appengine.serviceAdmin"
```

### Confirmation
> Deployed service [default] to [https://twisterrob-cinema.appspot.com]
