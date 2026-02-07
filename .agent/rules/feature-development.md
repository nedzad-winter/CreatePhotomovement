---
trigger: always_on
---

You can find the create mod in _create, no need to google for the code.

When developing new features like new blocks, document the development in the current-dev-satus.md.
Things that should be documented:
- Status of minceraft version: Is the block ported to all other versions. The primary focus is 1.21.1 neoforge, but porting to 1.20.1 neoforge/forge and fabric needs to be done after I am satisfied with the status quo.


**Code Doucumentation**
Create a document code-documentation.md in the in the .brain folder.
Document the flow of the code, the tricks we did to make something work, why we did it like that and if we are developing in neoforge 1.21.1 we need to make notes what needs
to be done when porting back to neoforge/forge and fabric on version 1.20.1

**Changelogs**
The Changelog.md should be updated when the user is happy and want to release