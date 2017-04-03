#!/bin/bash

# http://www.tecmint.com/rsync-local-remote-file-synchronization-commands/

# rsync -vazh --progress --delete --checksum --dry-run /home/development/opt/jee-neon/workspace/ActionCollection /home/development/GitHub/ActionCollection
# rsync -vazh --progress --delete --checksum --dry-run /home/development/opt/jee-neon/workspace/ActionCollectionUnitTest /home/development/GitHub/ActionCollection

echo "Checking ActionCollection -> ActionCollection"
rsync -vazh --progress --delete --checksum /home/development/opt/jee-neon/workspace/ActionCollection /home/development/GitHub/ActionCollection
echo "Checking ActionCollection -> ActionCollectionUnitTest"
rsync -vazh --progress --delete --checksum /home/development/opt/jee-neon/workspace/ActionCollectionUnitTest /home/development/GitHub/ActionCollection




# rsync -vazh --progress --delete --checksum --dry-run /home/development/opt/jee-neon/workspace/ElsuDatabase /home/development/GitHub/ElsuFoundation
# rsync -vazh --progress --delete --checksum --dry-run /home/development/opt/jee-neon/workspace/ElsuFoundation /home/development/GitHub/ElsuFoundation
# rsync -vazh --progress --delete --checksum --dry-run /home/development/opt/jee-neon/workspace/ElsuFoundationTest /home/development/GitHub/ElsuFoundation
# rsync -vazh --progress --delete --checksum --dry-run /home/development/opt/jee-neon/workspace/ElsuSharedLibraries /home/development/GitHub/ElsuFoundation

echo "Checking ElsuFoundation -> ElsuDatabase"
rsync -vazh --progress --delete --checksum /home/development/opt/jee-neon/workspace/ElsuDatabase /home/development/GitHub/ElsuFoundation
echo "Checking ElsuFoundation -> ElsuFoundation"
rsync -vazh --progress --delete --checksum /home/development/opt/jee-neon/workspace/ElsuFoundation /home/development/GitHub/ElsuFoundation
echo "Checking ElsuFoundation -> ElsuFoundationTest"
rsync -vazh --progress --delete --checksum /home/development/opt/jee-neon/workspace/ElsuFoundationTest /home/development/GitHub/ElsuFoundation
echo "Checking ElsuFoundation -> ElsuSharedLibraries"
rsync -vazh --progress --delete --checksum /home/development/opt/jee-neon/workspace/ElsuSharedLibraries /home/development/GitHub/ElsuFoundation




# rsync -vazh --progress --delete --checksum --dry-run /home/development/opt/jee-neon/workspace/CG1VCatalogServices /home/development/GitHub/CG1V/widgets
# rsync -vazh --progress --delete --checksum --dry-run /home/development/opt/jee-neon/workspace/CG1VCatalogWidget /home/development/GitHub/CG1V/widgets
# rsync -vazh --progress --delete --checksum --dry-run /home/development/opt/jee-neon/workspace/CG1VDirectoryServices /home/development/GitHub/CG1V/widgets
# rsync -vazh --progress --delete --checksum --dry-run /home/development/opt/jee-neon/workspace/EnterpriseDirectoryServices /home/development/GitHub/CG1V/widgets
# rsync -vazh --progress --delete --checksum --dry-run /home/development/opt/jee-neon/workspace/UCOPDirectoryServices /home/development/GitHub/CG1V/widgets
# rsync -vazh --progress --delete --checksum --dry-run /home/development/opt/jee-neon/workspace/UCOPTrackWidget /home/development/GitHub/CG1V/widgets

echo "Checking CG1V/widgets -> CG1VCatalogServices"
rsync -vazh --progress --delete --checksum /home/development/opt/jee-neon/workspace/CG1VCatalogServices /home/development/GitHub/CG1V/widgets
echo "Checking ElsuFoundation -> CG1VCatalogWidget"
rsync -vazh --progress --delete --checksum /home/development/opt/jee-neon/workspace/CG1VCatalogWidget /home/development/GitHub/CG1V/widgets
echo "Checking ElsuFoundation -> CG1VDirectoryServices"
rsync -vazh --progress --delete --checksum /home/development/opt/jee-neon/workspace/CG1VDirectoryServices /home/development/GitHub/CG1V/widgets
echo "Checking ElsuFoundation -> EnterpriseDirectoryServices"
rsync -vazh --progress --delete --checksum /home/development/opt/jee-neon/workspace/EnterpriseDirectoryServices /home/development/GitHub/CG1V/widgets
echo "Checking ElsuFoundation -> UCOPDirectoryServices"
rsync -vazh --progress --delete --checksum /home/development/opt/jee-neon/workspace/UCOPDirectoryServices /home/development/GitHub/CG1V/widgets
echo "Checking ElsuFoundation -> UCOPTrackWidget"
rsync -vazh --progress --delete --checksum /home/development/opt/jee-neon/workspace/UCOPTrackWidget /home/development/GitHub/CG1V/widgets
