#!/bin/bash

# http://www.tecmint.com/rsync-local-remote-file-synchronization-commands/

# rsync -vazh --progress --delete --checksum --dry-run /home/development/GitHub/ActionCollection/ActionCollection /home/development/opt/jee-neon/workspace
# rsync -vazh --progress --delete --checksum --dry-run /home/development/GitHub/ActionCollection/ActionCollectionUnitTest /home/development/opt/jee-neon/workspace

echo "Checking ActionCollection <- ActionCollection"
rsync -vazh --progress --delete --checksum /home/development/GitHub/ActionCollection/ActionCollection /home/development/opt/jee-neon/workspace
echo "Checking ActionCollection <- ActionCollectionUnitTest"
rsync -vazh --progress --delete --checksum /home/development/GitHub/ActionCollection/ActionCollectionUnitTest /home/development/opt/jee-neon/workspace




# rsync -vazh --progress --delete --checksum --dry-run /home/development/GitHub/ElsuFoundation/ElsuDatabase /home/development/opt/jee-neon/workspace
# rsync -vazh --progress --delete --checksum --dry-run /home/development/GitHub/ElsuFoundation/ElsuFoundation /home/development/opt/jee-neon/workspace
# rsync -vazh --progress --delete --checksum --dry-run /home/development/GitHub/ElsuFoundation/ElsuFoundationTest /home/development/opt/jee-neon/workspace
# rsync -vazh --progress --delete --checksum --dry-run /home/development/GitHub/ElsuFoundation/ElsuSharedLibraries /home/development/opt/jee-neon/workspace

echo "Checking ElsuFoundation <- ElsuDatabase"
rsync -vazh --progress --delete --checksum /home/development/GitHub/ElsuFoundation/ElsuDatabase /home/development/opt/jee-neon/workspace
echo "Checking ElsuFoundation <- ElsuFoundation"
rsync -vazh --progress --delete --checksum /home/development/GitHub/ElsuFoundation/ElsuFoundation /home/development/opt/jee-neon/workspace
echo "Checking ElsuFoundation <- ElsuFoundationTest"
rsync -vazh --progress --delete --checksum /home/development/GitHub/ElsuFoundation/ElsuFoundationTest /home/development/opt/jee-neon/workspace
echo "Checking ElsuFoundation <- ElsuSharedLibraries"
rsync -vazh --progress --delete --checksum /home/development/GitHub/ElsuFoundation/ElsuSharedLibraries /home/development/opt/jee-neon/workspace




# rsync -vazh --progress --delete --checksum --dry-run /home/development/GitHub/CG1V/widgets/CG1VCatalogServices /home/development/opt/jee-neon/workspace
# rsync -vazh --progress --delete --checksum --dry-run /home/development/GitHub/CG1V/widgets/CG1VCatalogWidget /home/development/opt/jee-neon/workspace
# rsync -vazh --progress --delete --checksum --dry-run /home/development/GitHub/CG1V/widgets/CG1VDirectoryServices /home/development/opt/jee-neon/workspace
# rsync -vazh --progress --delete --checksum --dry-run /home/development/GitHub/CG1V/widgets/EnterpriseDirectoryServices /home/development/opt/jee-neon/workspace
# rsync -vazh --progress --delete --checksum --dry-run /home/development/GitHub/CG1V/widgets/UCOPDirectoryServices /home/development/opt/jee-neon/workspace
# rsync -vazh --progress --delete --checksum --dry-run /home/development/GitHub/CG1V/widgets/UCOPTrackWidget /home/development/opt/jee-neon/workspace

echo "Checking CG1V/widgets <- CG1VCatalogServices"
rsync -vazh --progress --delete --checksum /home/development/GitHub/CG1V/widgets/CG1VCatalogServices /home/development/opt/jee-neon/workspace
echo "Checking ElsuFoundation <- CG1VCatalogWidget"
rsync -vazh --progress --delete --checksum /home/development/GitHub/CG1V/widgets/CG1VCatalogWidget /home/development/opt/jee-neon/workspace
echo "Checking ElsuFoundation <- CG1VDirectoryServices"
rsync -vazh --progress --delete --checksum /home/development/GitHub/CG1V/widgets/CG1VDirectoryServices /home/development/opt/jee-neon/workspace
echo "Checking ElsuFoundation <- EnterpriseDirectoryServices"
rsync -vazh --progress --delete --checksum /home/development/GitHub/CG1V/widgets/EnterpriseDirectoryServices /home/development/opt/jee-neon/workspace
echo "Checking ElsuFoundation <- UCOPDirectoryServices"
rsync -vazh --progress --delete --checksum /home/development/GitHub/CG1V/widgets/UCOPDirectoryServices /home/development/opt/jee-neon/workspace
echo "Checking ElsuFoundation <- UCOPTrackWidget"
rsync -vazh --progress --delete --checksum /home/development/GitHub/CG1V/widgets/UCOPTrackWidget /home/development/opt/jee-neon/workspace
